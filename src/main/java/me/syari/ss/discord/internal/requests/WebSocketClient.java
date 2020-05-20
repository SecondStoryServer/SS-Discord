package me.syari.ss.discord.internal.requests;

import com.neovisionaries.ws.client.*;
import me.syari.ss.discord.api.requests.CloseCode;
import me.syari.ss.discord.api.SessionController;
import me.syari.ss.discord.api.data.DataArray;
import me.syari.ss.discord.api.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.handle.GuildCreateHandler;
import me.syari.ss.discord.internal.handle.MessageCreateHandler;
import me.syari.ss.discord.internal.utils.ZlibDecompressor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;

public class WebSocketClient extends WebSocketAdapter implements WebSocketListener {
    private static final int DISCORD_GATEWAY_VERSION = 6;
    private static final String INVALIDATE_REASON = "INVALIDATE_SESSION";
    private static final long IDENTIFY_BACKOFF = TimeUnit.SECONDS.toMillis(SessionController.IDENTIFY_DELAY);

    private final JDA api;
    public WebSocket socket;
    private String sessionId = null;
    private final Object readLock = new Object();
    private final ZlibDecompressor decompressor = new ZlibDecompressor();
    public final ReentrantLock queueLock = new ReentrantLock();
    public final ScheduledExecutorService executor;
    private WebSocketSendingThread ratelimitThread;
    private volatile Future<?> keepAliveThread;
    private boolean initiating;
    private int reconnectTimeoutS = 2;
    private long identifyTime = 0;
    public final Queue<String> chunkSyncQueue = new ConcurrentLinkedQueue<>();
    public final Queue<String> ratelimitQueue = new ConcurrentLinkedQueue<>();
    private volatile long ratelimitResetTime;
    private final AtomicInteger messagesSent = new AtomicInteger(0);
    private volatile boolean shutdown = false;
    private boolean shouldReconnect;
    private boolean handleIdentifyRateLimit = false;
    private boolean connected = false;
    public volatile boolean sentAuthInfo = false;
    private volatile SessionController.SessionConnectNode connectNode;

    public WebSocketClient(@NotNull JDA api) {
        this.api = api;
        this.executor = api.getGatewayPool();
        this.shouldReconnect = true;
        this.connectNode = new StartingNode();
        try {
            api.getSessionController().appendSession(connectNode);
        } catch (RuntimeException | Error e) {
            this.api.setStatus(JDA.Status.SHUTDOWN);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw (Error) e;
            }
        }
    }

    public JDA getJDA() {
        return api;
    }

    public void ready() {
        if (initiating) {
            initiating = false;
        }
        api.setStatus(JDA.Status.CONNECTED);
    }

    public boolean isReady() {
        return !initiating;
    }

    public void handle(@NotNull List<DataObject> events) {
        events.forEach(this::onDispatch);
    }

    public void chunkOrSyncRequest(DataObject request) {
        locked(() -> chunkSyncQueue.add(request.toString()));
    }

    protected boolean send(String message, boolean skipQueue) {
        if (!connected) return false;
        long now = System.currentTimeMillis();
        if (this.ratelimitResetTime <= now) {
            this.messagesSent.set(0);
            this.ratelimitResetTime = now + 60000;
        }
        if (this.messagesSent.get() <= 115 || (skipQueue && this.messagesSent.get() <= 119)) {
            socket.sendText(message);
            this.messagesSent.getAndIncrement();
            return true;
        } else {
            return false;
        }
    }

    protected void setupSendingThread() {
        ratelimitThread = new WebSocketSendingThread(this);
        ratelimitThread.start();
    }

    public void close() {
        if (socket != null) socket.sendClose(1000);
    }

    public void close(int code, String reason) {
        if (socket != null) socket.sendClose(code, reason);
    }

    public synchronized void shutdown() {
        shutdown = true;
        shouldReconnect = false;
        if (connectNode != null) api.getSessionController().removeSession(connectNode);
        close(1000, "Shutting down");
    }

    protected synchronized void connect() {
        if (api.getStatus() != JDA.Status.ATTEMPTING_TO_RECONNECT) api.setStatus(JDA.Status.CONNECTING_TO_WEBSOCKET);
        if (shutdown) throw new RejectedExecutionException("JDA is shutdown!");
        initiating = true;
        String url = api.getGatewayUrl() + "?encoding=json&v=" + DISCORD_GATEWAY_VERSION + "&compress=zlib-stream";
        try {
            WebSocketFactory socketFactory = api.getWebSocketFactory();
            synchronized (socketFactory) {
                String host = URI.create(url).getHost();
                if (host != null) {
                    socketFactory.setServerName(host);
                } else {
                    socketFactory.setServerNames(null);
                }
                socket = socketFactory.createSocket(url);
            }
            socket.addHeader("Accept-Encoding", "gzip").addListener(this).connect();
        } catch (IOException | WebSocketException ex) {
            api.resetGatewayUrl();
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) {
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        api.setStatus(JDA.Status.IDENTIFYING_SESSION);
        connected = true;
        messagesSent.set(0);
        ratelimitResetTime = System.currentTimeMillis() + 60000;
        if (sessionId == null) {
            sendIdentify();
        } else {
            sendResume();
        }
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
        sentAuthInfo = false;
        connected = false;
        api.setStatus(JDA.Status.DISCONNECTED);
        CloseCode closeCode = null;
        int rawCloseCode;
        boolean isInvalidate = false;
        if (keepAliveThread != null) {
            keepAliveThread.cancel(false);
            keepAliveThread = null;
        }
        if (serverCloseFrame != null) {
            rawCloseCode = serverCloseFrame.getCloseCode();
            closeCode = CloseCode.from(rawCloseCode);
        }
        if (clientCloseFrame != null && clientCloseFrame.getCloseCode() == 1000 && Objects.equals(clientCloseFrame.getCloseReason(), INVALIDATE_REASON)) {
            isInvalidate = true;
        }

        boolean closeCodeIsReconnect = closeCode == null || closeCode.isReconnect();
        if (!shouldReconnect || !closeCodeIsReconnect || executor.isShutdown()) {
            if (ratelimitThread != null) {
                ratelimitThread.shutdown();
                ratelimitThread = null;
            }
            decompressor.reset();
            api.shutdownInternals();
        } else {
            synchronized (readLock) {
                decompressor.reset();
            }
            if (isInvalidate) invalidate();
            try {
                handleReconnect();
            } catch (InterruptedException e) {
                invalidate();
                queueReconnect();
            }
        }
    }

    private void handleReconnect() throws InterruptedException {
        if (sessionId == null) {
            queueReconnect();
        } else {
            reconnect();
        }
    }

    protected void queueReconnect() {
        try {
            this.api.setStatus(JDA.Status.RECONNECT_QUEUED);
            this.connectNode = new ReconnectNode();
            this.api.getSessionController().appendSession(connectNode);
        } catch (IllegalStateException ex) {
            this.api.setStatus(JDA.Status.SHUTDOWN);
        }
    }

    protected void reconnect() throws InterruptedException {
        if (shutdown) {
            api.setStatus(JDA.Status.SHUTDOWN);
            return;
        }
        while (shouldReconnect) {
            api.setStatus(JDA.Status.WAITING_TO_RECONNECT);
            int delay = reconnectTimeoutS;
            reconnectTimeoutS = Math.min(reconnectTimeoutS << 1, 900);
            Thread.sleep(delay * 1000);
            handleIdentifyRateLimit = false;
            api.setStatus(JDA.Status.ATTEMPTING_TO_RECONNECT);
            try {
                connect();
                break;
            } catch (RejectedExecutionException ex) {
                api.setStatus(JDA.Status.SHUTDOWN);
                return;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void setupKeepAlive(long timeout) {
        keepAliveThread = executor.scheduleAtFixedRate(() ->
        {
            if (connected) sendKeepAlive();
        }, 0, timeout, TimeUnit.MILLISECONDS);
    }

    protected void sendKeepAlive() {
        String keepAlivePacket = DataObject.empty().put("op", WebSocketCode.HEARTBEAT).put("d", api.getResponseTotal()).toString();
        send(keepAlivePacket, true);
    }

    protected void sendIdentify() {
        DataObject connectionProperties = DataObject.empty()
                .put("$os", System.getProperty("os.name"))
                .put("$browser", "JDA")
                .put("$device", "JDA")
                .put("$referring_domain", "")
                .put("$referrer", "");
        DataObject payload = DataObject.empty()
                .put("token", getToken())
                .put("properties", connectionProperties)
                .put("v", DISCORD_GATEWAY_VERSION)
                .put("guild_subscriptions", true)
                .put("large_threshold", 250);
        DataObject identify = DataObject.empty()
                .put("op", WebSocketCode.IDENTIFY)
                .put("d", payload);
        payload.put("shard", DataArray.empty()
                .add(0)
                .add(1));
        send(identify.toString(), true);
        handleIdentifyRateLimit = true;
        identifyTime = System.currentTimeMillis();
        sentAuthInfo = true;
        api.setStatus(JDA.Status.AWAITING_LOGIN_CONFIRMATION);
    }

    protected void sendResume() {
        DataObject resume = DataObject.empty()
                .put("op", WebSocketCode.RESUME)
                .put("d", DataObject.empty().put("session_id", sessionId).put("token", getToken()).put("seq", api.getResponseTotal()));
        send(resume.toString(), true);
        api.setStatus(JDA.Status.AWAITING_LOGIN_CONFIRMATION);
    }

    protected void invalidate() {
        sessionId = null;
        sentAuthInfo = false;
        locked(chunkSyncQueue::clear);
        api.getEventCache().clear();
        api.getGuildSetupController().clearCache();
    }

    protected String getToken() {
        return api.getToken().substring("Bot ".length());
    }

    protected void handleEvent(DataObject content) {
        try {
            onEvent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void onEvent(@NotNull DataObject content) {
        int opCode = content.getInt("op");
        if (!content.isNull("s")) api.setResponseTotal(content.getInt("s"));
        switch (opCode) {
            case WebSocketCode.DISPATCH:
                onDispatch(content);
                break;
            case WebSocketCode.HEARTBEAT:
                sendKeepAlive();
                break;
            case WebSocketCode.RECONNECT:
                close(4000, "OP 7: RECONNECT");
                break;
            case WebSocketCode.INVALIDATE_SESSION:
                handleIdentifyRateLimit = handleIdentifyRateLimit && System.currentTimeMillis() - identifyTime < IDENTIFY_BACKOFF;
                sentAuthInfo = false;
                final boolean isResume = content.getBoolean("d", false);
                int closeCode = isResume ? 4000 : 1000;
                if (!isResume) {
                    invalidate();
                }
                close(closeCode, INVALIDATE_REASON);
                break;
            case WebSocketCode.HELLO:
                final DataObject data = content.getObject("d");
                setupKeepAlive(data.getLong("heartbeat_interval"));
                break;
        }
    }

    protected void onDispatch(@NotNull DataObject raw) {
        String type = raw.getString("t");
        long responseTotal = api.getResponseTotal();
        if (!(raw.get("d") instanceof Map)) {
            return;
        }
        DataObject content = raw.getObject("d");
        JDA jda = getJDA();
        try {
            switch (type) {
                case "READY":
                    reconnectTimeoutS = 2;
                    api.setStatus(JDA.Status.LOADING_SUBSYSTEMS);
                    handleIdentifyRateLimit = false;
                    sessionId = content.getString("session_id");
                    break;
                case "GUILD_CREATE":
                    new GuildCreateHandler(api).handle(responseTotal, raw);
                    break;
                case "MESSAGE_CREATE":
                    new MessageCreateHandler(api).handle(responseTotal, raw);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (responseTotal % EventCache.TIMEOUT_AMOUNT == 0) jda.getEventCache().timeout(responseTotal);
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws DataFormatException {
        DataObject json;
        synchronized (readLock) {
            json = handleBinary(binary);
        }
        if (json != null) handleEvent(json);
    }

    protected DataObject handleBinary(byte[] binary) throws DataFormatException {
        String json;
        try {
            json = decompressor.decompress(binary);
            if (json == null) return null;
        } catch (DataFormatException ex) {
            close(4000, "MALFORMED_PACKAGE");
            throw ex;
        }

        return DataObject.fromJson(json);
    }

    protected void maybeUnlock() {
        if (queueLock.isHeldByCurrentThread()) queueLock.unlock();
    }

    protected void locked(@NotNull Runnable task) {
        try {
            queueLock.lockInterruptibly();
            task.run();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            maybeUnlock();
        }
    }

    protected <T> void locked(@NotNull Supplier<T> task) {
        try {
            queueLock.lockInterruptibly();
            task.get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            maybeUnlock();
        }
    }

    private class StartingNode implements SessionController.SessionConnectNode {
        @Override
        public void run(boolean isLast) throws InterruptedException {
            if (shutdown) return;
            setupSendingThread();
            connect();
            if (isLast) return;
            try {
                api.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED);
            } catch (IllegalStateException ex) {
                close();
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash("C", getJDA());
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) return true;
            return other instanceof StartingNode;
        }
    }

    private class ReconnectNode implements SessionController.SessionConnectNode {
        @Override
        public void run(boolean isLast) throws InterruptedException {
            if (shutdown) return;
            reconnect();
            if (isLast) return;
            try {
                api.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED);
            } catch (IllegalStateException ex) {
                close();
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash("R", getJDA());
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) return true;
            return object instanceof ReconnectNode;
        }
    }
}
