package me.syari.ss.discord.internal.requests;

import com.neovisionaries.ws.client.*;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.ParsingException;
import me.syari.ss.discord.api.requests.CloseCode;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.api.utils.data.DataType;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.handle.*;
import me.syari.ss.discord.internal.utils.IOUtil;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.compress.Decompressor;
import me.syari.ss.discord.internal.utils.compress.ZlibDecompressor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class WebSocketClient extends WebSocketAdapter implements WebSocketListener {
    public static final Logger LOG = JDALogger.getLog(WebSocketClient.class);
    public static final int DISCORD_GATEWAY_VERSION = 6;

    protected static final String INVALIDATE_REASON = "INVALIDATE_SESSION";
    protected static final long IDENTIFY_BACKOFF = TimeUnit.SECONDS.toMillis(SessionController.IDENTIFY_DELAY);

    protected final JDAImpl api;
    protected final JDA.ShardInfo shardInfo;
    protected final Map<String, SocketHandler> handlers = new HashMap<>();

    public WebSocket socket;
    protected String sessionId = null;
    protected final Object readLock = new Object();
    protected Decompressor decompressor;

    protected final ReentrantLock queueLock = new ReentrantLock();
    protected final ScheduledExecutorService executor;
    protected WebSocketSendingThread ratelimitThread;
    protected volatile Future<?> keepAliveThread;

    protected boolean initiating;

    protected int reconnectTimeoutS = 2;
    protected long identifyTime = 0;

    protected final Queue<String> chunkSyncQueue = new ConcurrentLinkedQueue<>();
    protected final Queue<String> ratelimitQueue = new ConcurrentLinkedQueue<>();

    protected volatile long ratelimitResetTime;
    protected final AtomicInteger messagesSent = new AtomicInteger(0);

    protected volatile boolean shutdown = false;
    protected boolean shouldReconnect;
    protected boolean handleIdentifyRateLimit = false;
    protected boolean connected = false;

    protected volatile boolean printedRateLimitMessage = false;
    protected volatile boolean sentAuthInfo = false;
    protected boolean firstInit = true;
    protected boolean processingReady = true;

    protected volatile ConnectNode connectNode;

    public WebSocketClient(@NotNull JDAImpl api) {
        this.api = api;
        this.executor = api.getGatewayPool();
        this.shardInfo = JDA.ShardInfo.SINGLE;
        this.shouldReconnect = api.isAutoReconnect();
        this.connectNode = new StartingNode();
        setupHandlers();
        try {
            api.getSessionController().appendSession(connectNode);
        } catch (RuntimeException | Error e) {
            LOG.error("Failed to append new session to session controller queue. Shutting down!", e);
            this.api.setStatus(JDA.Status.SHUTDOWN);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw (Error) e;
        }
    }

    public JDA getJDA() {
        return api;
    }

    public void ready() {
        if (initiating) {
            initiating = false;
            processingReady = false;
            if (firstInit) {
                firstInit = false;
                if (api.getGuilds().size() >= 2000) {
                    JDAImpl.LOG.warn(" __      __ _    ___  _  _  ___  _  _   ___  _ ");
                    JDAImpl.LOG.warn(" \\ \\    / //_\\  | _ \\| \\| ||_ _|| \\| | / __|| |");
                    JDAImpl.LOG.warn("  \\ \\/\\/ // _ \\ |   /| .` | | | | .` || (_ ||_|");
                    JDAImpl.LOG.warn("   \\_/\\_//_/ \\_\\|_|_\\|_|\\_||___||_|\\_| \\___|(_)");
                    JDAImpl.LOG.warn("You're running a session with over 2000 connected");
                    JDAImpl.LOG.warn("guilds. You should shard the connection in order");
                    JDAImpl.LOG.warn("to split the load or things like resuming");
                    JDAImpl.LOG.warn("connection might not work as expected.");
                    JDAImpl.LOG.warn("For more info see https://git.io/vrFWP");
                }
                JDAImpl.LOG.info("Finished Loading!");
            } else {
                JDAImpl.LOG.info("Finished (Re)Loading!");
            }
        } else {
            JDAImpl.LOG.info("Successfully resumed Session!");
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
        if (!connected)
            return false;

        long now = System.currentTimeMillis();

        if (this.ratelimitResetTime <= now) {
            this.messagesSent.set(0);
            this.ratelimitResetTime = now + 60000;
            this.printedRateLimitMessage = false;
        }

        if (this.messagesSent.get() <= 115 || (skipQueue && this.messagesSent.get() <= 119)) {
            LOG.trace("<- {}", message);
            socket.sendText(message);
            this.messagesSent.getAndIncrement();
            return true;
        } else {
            if (!printedRateLimitMessage) {
                LOG.warn("Hit the WebSocket RateLimit! This can be caused by too many presence or voice status updates (connect/disconnect/mute/deaf). Regular: {} Chunking: {}", ratelimitQueue.size(), chunkSyncQueue.size());
                printedRateLimitMessage = true;
            }
            return false;
        }
    }

    protected void setupSendingThread() {
        ratelimitThread = new WebSocketSendingThread(this);
        ratelimitThread.start();
    }

    public void close() {
        if (socket != null)
            socket.sendClose(1000);
    }

    public void close(int code, String reason) {
        if (socket != null)
            socket.sendClose(code, reason);
    }

    public synchronized void shutdown() {
        shutdown = true;
        shouldReconnect = false;
        if (connectNode != null)
            api.getSessionController().removeSession(connectNode);
        close(1000, "Shutting down");
    }

    protected synchronized void connect() {
        if (api.getStatus() != JDA.Status.ATTEMPTING_TO_RECONNECT)
            api.setStatus(JDA.Status.CONNECTING_TO_WEBSOCKET);
        if (shutdown)
            throw new RejectedExecutionException("JDA is shutdown!");
        initiating = true;

        String url = api.getGatewayUrl() + "?encoding=json&v=" + DISCORD_GATEWAY_VERSION + "&compress=zlib-stream";
        if (decompressor == null)
            decompressor = new ZlibDecompressor(api.getMaxBufferSize());

        try {
            WebSocketFactory socketFactory = api.getWebSocketFactory();
            synchronized (socketFactory) {
                String host = IOUtil.getHost(url);
                if (host != null) {
                    socketFactory.setServerName(host);
                } else {
                    socketFactory.setServerNames(null);
                }
                socket = socketFactory.createSocket(url);
            }
            socket.addHeader("Accept-Encoding", "gzip")
                    .addListener(this)
                    .connect();
        } catch (IOException | WebSocketException e) {
            api.resetGatewayUrl();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) {
        api.setContext();
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        api.setStatus(JDA.Status.IDENTIFYING_SESSION);
        if (sessionId == null) //no need to log for resume here
            LOG.info("Connected to WebSocket");
        else
            LOG.debug("Connected to WebSocket");
        connected = true;
        messagesSent.set(0);
        ratelimitResetTime = System.currentTimeMillis() + 60000;
        if (sessionId == null)
            sendIdentify();
        else
            sendResume();
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
            String rawCloseReason = serverCloseFrame.getCloseReason();
            closeCode = CloseCode.from(rawCloseCode);
            if (closeCode == CloseCode.RATE_LIMITED)
                LOG.error("WebSocket connection closed due to ratelimit! Sent more than 120 websocket messages in under 60 seconds!");
            else if (closeCode != null)
                LOG.debug("WebSocket connection closed with code {}", closeCode);
            else if (rawCloseReason != null)
                LOG.warn("WebSocket connection closed with code {}: {}", rawCloseCode, rawCloseReason);
            else
                LOG.warn("WebSocket connection closed with unknown meaning for close-code {}", rawCloseCode);
        }
        if (clientCloseFrame != null
                && clientCloseFrame.getCloseCode() == 1000
                && Objects.equals(clientCloseFrame.getCloseReason(), INVALIDATE_REASON)) {
            isInvalidate = true;
        }

        boolean closeCodeIsReconnect = closeCode == null || closeCode.isReconnect();
        if (!shouldReconnect || !closeCodeIsReconnect || executor.isShutdown()) {
            if (ratelimitThread != null) {
                ratelimitThread.shutdown();
                ratelimitThread = null;
            }

            if (!closeCodeIsReconnect) {
                LOG.error("WebSocket connection was closed and cannot be recovered due to identification issues\n{}", closeCode);
            }

            if (decompressor != null)
                decompressor.shutdown();
            api.shutdownInternals();
        } else {
            synchronized (readLock) {
                if (decompressor != null)
                    decompressor.reset();
            }
            if (isInvalidate)
                invalidate();
            try {
                handleReconnect();
            } catch (InterruptedException e) {
                LOG.error("Failed to resume due to interrupted thread", e);
                invalidate();
                queueReconnect();
            }
        }
    }

    private void handleReconnect() throws InterruptedException {
        if (sessionId == null) {
            if (handleIdentifyRateLimit) {
                long backoff = calculateIdentifyBackoff();
                if (backoff > 0) {
                    LOG.error("Encountered IDENTIFY Rate Limit! Waiting {} milliseconds before trying again!", backoff);
                    Thread.sleep(backoff);
                } else {
                    LOG.error("Encountered IDENTIFY Rate Limit!");
                }
            }
            LOG.warn("Got disconnected from WebSocket. Appending to reconnect queue");
            queueReconnect();
        } else {
            LOG.warn("Got disconnected from WebSocket. Attempting to resume session");
            reconnect();
        }
    }

    protected long calculateIdentifyBackoff() {
        long currentTime = System.currentTimeMillis();
        return currentTime - (identifyTime + IDENTIFY_BACKOFF);
    }

    protected void queueReconnect() {
        try {
            this.api.setStatus(JDA.Status.RECONNECT_QUEUED);
            this.connectNode = new ReconnectNode();
            this.api.getSessionController().appendSession(connectNode);
        } catch (IllegalStateException ex) {
            LOG.error("Reconnect queue rejected session. Shutting down...");
            this.api.setStatus(JDA.Status.SHUTDOWN);
        }
    }

    protected void reconnect() throws InterruptedException {
        reconnect(false);
    }


    public void reconnect(boolean callFromQueue) throws InterruptedException {
        Set<MDC.MDCCloseable> contextEntries = null;
        Map<String, String> previousContext = null;
        {
            ConcurrentMap<String, String> contextMap = api.getContextMap();
            if (callFromQueue && contextMap != null) {
                previousContext = MDC.getCopyOfContextMap();
                contextEntries = contextMap.entrySet().stream()
                        .map((entry) -> MDC.putCloseable(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toSet());
            }
        }
        if (shutdown) {
            api.setStatus(JDA.Status.SHUTDOWN);
            return;
        }
        String message = "";
        if (callFromQueue)
            message = String.format("Queue is attempting to reconnect a shard...%s ", shardInfo != null ? " Shard: " + shardInfo.getShardString() : "");
        LOG.debug("{}Attempting to reconnect in {}s", message, reconnectTimeoutS);
        while (shouldReconnect) {
            api.setStatus(JDA.Status.WAITING_TO_RECONNECT);
            int delay = reconnectTimeoutS;
            reconnectTimeoutS = Math.min(reconnectTimeoutS << 1, api.getMaxReconnectDelay());
            Thread.sleep(delay * 1000);
            handleIdentifyRateLimit = false;
            api.setStatus(JDA.Status.ATTEMPTING_TO_RECONNECT);
            LOG.debug("Attempting to reconnect!");
            try {
                connect();
                break;
            } catch (RejectedExecutionException ex) {
                api.setStatus(JDA.Status.SHUTDOWN);
                return;
            } catch (RuntimeException ex) {
                LOG.warn("Reconnect failed! Next attempt in {}s", reconnectTimeoutS);
            }
        }
        if (contextEntries != null)
            contextEntries.forEach(MDC.MDCCloseable::close);
        if (previousContext != null)
            previousContext.forEach(MDC::put);
    }

    protected void setupKeepAlive(long timeout) {
        keepAliveThread = executor.scheduleAtFixedRate(() ->
        {
            api.setContext();
            if (connected)
                sendKeepAlive();
        }, 0, timeout, TimeUnit.MILLISECONDS);
    }

    protected void sendKeepAlive() {
        String keepAlivePacket = DataObject.empty().put("op", WebSocketCode.HEARTBEAT).put("d", api.getResponseTotal()).toString();

        send(keepAlivePacket, true);
    }

    protected void sendIdentify() {
        LOG.debug("Sending Identify-packet...");
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
                .put("guild_subscriptions", api.isGuildSubscriptions())
                .put("large_threshold", api.getLargeThreshold());
        DataObject identify = DataObject.empty()
                .put("op", WebSocketCode.IDENTIFY)
                .put("d", payload);
        if (shardInfo != null) {
            payload.put("shard", DataArray.empty()
                    .add(shardInfo.getShardId())
                    .add(shardInfo.getShardTotal()));
        }
        send(identify.toString(), true);
        handleIdentifyRateLimit = true;
        identifyTime = System.currentTimeMillis();
        sentAuthInfo = true;
        api.setStatus(JDA.Status.AWAITING_LOGIN_CONFIRMATION);
    }

    protected void sendResume() {
        LOG.debug("Sending Resume-packet...");
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

        api.getTextChannelsView().clear();
        api.getGuildsView().clear();
        api.getUsersView().clear();
        api.getFakeUserMap().clear();
        api.getEventCache().clear();
        api.getGuildSetupController().clearCache();
    }

    protected String getToken() {
        return api.getToken().substring("Bot ".length());
    }

    protected List<DataObject> convertPresencesReplace(long responseTotal, @NotNull DataArray array) {
        List<DataObject> output = new LinkedList<>();
        for (int i = 0; i < array.length(); i++) {
            DataObject presence = array.getObject(i);
            final DataObject obj = DataObject.empty();
            obj.put("comment", "This was constructed from a PRESENCES_REPLACE payload")
                    .put("op", WebSocketCode.DISPATCH)
                    .put("s", responseTotal)
                    .put("d", presence)
                    .put("t", "PRESENCE_UPDATE");
            output.add(obj);
        }
        return output;
    }

    protected void handleEvent(DataObject content) {
        try {
            onEvent(content);
        } catch (Exception ex) {
            LOG.error("Encountered exception on lifecycle level\nJSON: {}", content, ex);
        }
    }

    protected void onEvent(@NotNull DataObject content) {
        int opCode = content.getInt("op");

        if (!content.isNull("s")) {
            api.setResponseTotal(content.getInt("s"));
        }

        switch (opCode) {
            case WebSocketCode.DISPATCH:
                onDispatch(content);
                break;
            case WebSocketCode.HEARTBEAT:
                LOG.debug("Got Keep-Alive request (OP 1). Sending response...");
                sendKeepAlive();
                break;
            case WebSocketCode.RECONNECT:
                LOG.debug("Got Reconnect request (OP 7). Closing connection now...");
                close(4000, "OP 7: RECONNECT");
                break;
            case WebSocketCode.INVALIDATE_SESSION:
                LOG.debug("Got Invalidate request (OP 9). Invalidating...");
                handleIdentifyRateLimit = handleIdentifyRateLimit && System.currentTimeMillis() - identifyTime < IDENTIFY_BACKOFF;

                sentAuthInfo = false;
                final boolean isResume = content.getBoolean("d");
                int closeCode = isResume ? 4000 : 1000;
                if (isResume) {
                    LOG.debug("Session can be recovered... Closing and sending new RESUME request");
                } else {
                    invalidate();
                }

                close(closeCode, INVALIDATE_REASON);
                break;
            case WebSocketCode.HELLO:
                LOG.debug("Got HELLO packet (OP 10). Initializing keep-alive.");
                final DataObject data = content.getObject("d");
                setupKeepAlive(data.getLong("heartbeat_interval"));
                break;
            default:
                LOG.debug("Got unknown op-code: {} with content: {}", opCode, content);
        }
    }

    protected void onDispatch(DataObject raw) {
        String type = raw.getString("t");
        long responseTotal = api.getResponseTotal();

        if (!raw.isType("d", DataType.OBJECT)) {
            if (type.equals("PRESENCES_REPLACE")) {
                final DataArray payload = raw.getArray("d");
                final List<DataObject> converted = convertPresencesReplace(responseTotal, payload);
                final SocketHandler handler = getHandler("PRESENCE_UPDATE");
                LOG.trace("{} -> {}", type, payload);
                for (DataObject object : converted) {
                    handler.handle(responseTotal, object);
                }
            } else {
                LOG.debug("Received event with unhandled body type JSON: {}", raw);
            }
            return;
        }

        DataObject content = raw.getObject("d");
        LOG.trace("{} -> {}", type, content);

        JDAImpl jda = (JDAImpl) getJDA();
        try {
            switch (type) {
                case "READY":
                    reconnectTimeoutS = 2;
                    api.setStatus(JDA.Status.LOADING_SUBSYSTEMS);
                    processingReady = true;
                    handleIdentifyRateLimit = false;
                    handlers.get("READY").handle(responseTotal, raw);
                    sessionId = content.getString("session_id");
                    break;
                case "RESUMED":
                    reconnectTimeoutS = 2;
                    sentAuthInfo = true;
                    if (!processingReady) {
                        initiating = false;
                        ready();
                    } else {
                        LOG.debug("Resumed while still processing initial ready");
                        jda.setStatus(JDA.Status.LOADING_SUBSYSTEMS);
                    }
                    break;
                default:
                    long guildId = content.getLong("guild_id", 0L);
                    if (api.isUnavailable(guildId) && !type.equals("GUILD_CREATE") && !type.equals("GUILD_DELETE")) {
                        LOG.warn("Ignoring {} for unavailable guild with id {}. JSON: {}", type, guildId, content);
                        break;
                    }
                    SocketHandler handler = handlers.get(type);
                    if (handler != null)
                        handler.handle(responseTotal, raw);
                    else
                        LOG.debug("Unrecognized event:\n{}", raw);
            }
        } catch (ParsingException ex) {
            LOG.warn("Got an unexpected Json-parse error. Please redirect following message to the devs:\n\t{}\n\t{} -> {}",
                    ex.getMessage(), type, content, ex);
        } catch (Exception ex) {
            LOG.error("Got an unexpected error. Please redirect following message to the devs:\n\t{} -> {}", type, content, ex);
        }

        if (responseTotal % EventCache.TIMEOUT_AMOUNT == 0)
            jda.getEventCache().timeout(responseTotal);
    }

    @Override
    public void onTextMessage(WebSocket websocket, String message) {
        handleEvent(DataObject.fromJson(message));
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws DataFormatException {
        DataObject json;
        synchronized (readLock) {
            json = handleBinary(binary);
        }
        if (json != null) {
            handleEvent(json);
        }
    }

    protected DataObject handleBinary(byte[] binary) throws DataFormatException {
        if (decompressor == null) {
            throw new IllegalStateException("Cannot decompress binary message due to unknown compression algorithm: ZLIB");
        }
        String jsonString;
        try {
            jsonString = decompressor.decompress(binary);
            if (jsonString == null) {
                return null;
            }
        } catch (DataFormatException e) {
            close(4000, "MALFORMED_PACKAGE");
            throw e;
        }

        try {
            return DataObject.fromJson(jsonString);
        } catch (ParsingException e) {
            LOG.error("Failed to parse json {}", jsonString);
            throw e;
        }
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
        handleCallbackError(websocket, cause);
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) {
        LOG.error("There was an error in the WebSocket connection", cause);
    }

    @Override
    public void onThreadCreated(WebSocket websocket, @NotNull ThreadType threadType, Thread thread) {
        String identifier = api.getIdentifierString();
        switch (threadType) {
            case CONNECT_THREAD:
                thread.setName(identifier + " MainWS-ConnectThread");
                break;
            case FINISH_THREAD:
                thread.setName(identifier + " MainWS-FinishThread");
                break;
            case READING_THREAD:
                thread.setName(identifier + " MainWS-ReadThread");
                break;
            case WRITING_THREAD:
                thread.setName(identifier + " MainWS-WriteThread");
                break;
            default:
                thread.setName(identifier + " MainWS-" + threadType);
        }
    }

    protected void maybeUnlock() {
        if (queueLock.isHeldByCurrentThread())
            queueLock.unlock();
    }

    protected void locked(@NotNull Runnable task) {
        try {
            queueLock.lockInterruptibly();
            task.run();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while trying to invalidate chunk/sync queue", e);
        } finally {
            maybeUnlock();
        }
    }

    protected <T> void locked(@NotNull Supplier<T> task) {
        try {
            queueLock.lockInterruptibly();
            task.get();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while trying to add chunk request", e);
        } finally {
            maybeUnlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SocketHandler> T getHandler(String type) {
        try {
            return (T) handlers.get(type);
        } catch (ClassCastException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setupHandlers() {
        final SocketHandler.NOPHandler nopHandler = new SocketHandler.NOPHandler(api);
        handlers.put("CHANNEL_CREATE", nopHandler);
        handlers.put("CHANNEL_DELETE", nopHandler);
        handlers.put("CHANNEL_UPDATE", nopHandler);
        handlers.put("GUILD_BAN_ADD", nopHandler);
        handlers.put("GUILD_BAN_REMOVE", nopHandler);
        handlers.put("GUILD_CREATE", new GuildCreateHandler(api));
        handlers.put("GUILD_DELETE", nopHandler);
        handlers.put("GUILD_EMOJIS_UPDATE", nopHandler);
        handlers.put("GUILD_MEMBER_ADD", nopHandler);
        handlers.put("GUILD_MEMBER_REMOVE", nopHandler);
        handlers.put("GUILD_MEMBER_UPDATE", nopHandler);
        handlers.put("GUILD_MEMBERS_CHUNK", nopHandler);
        handlers.put("GUILD_ROLE_CREATE", nopHandler);
        handlers.put("GUILD_ROLE_DELETE", nopHandler);
        handlers.put("GUILD_ROLE_UPDATE", nopHandler);
        handlers.put("GUILD_SYNC", nopHandler);
        handlers.put("GUILD_UPDATE", nopHandler);
        handlers.put("MESSAGE_CREATE", new MessageCreateHandler(api));
        handlers.put("MESSAGE_DELETE", nopHandler);
        handlers.put("MESSAGE_DELETE_BULK", nopHandler);
        handlers.put("MESSAGE_REACTION_ADD", nopHandler);
        handlers.put("MESSAGE_REACTION_REMOVE", nopHandler);
        handlers.put("MESSAGE_REACTION_REMOVE_ALL", nopHandler);
        handlers.put("MESSAGE_UPDATE", nopHandler);
        handlers.put("READY", new ReadyHandler(api));
        handlers.put("USER_UPDATE", nopHandler);
        handlers.put("VOICE_SERVER_UPDATE", nopHandler);
        handlers.put("VOICE_STATE_UPDATE", nopHandler);
        handlers.put("PRESENCE_UPDATE", nopHandler);
        handlers.put("TYPING_START", nopHandler);
        handlers.put("CHANNEL_PINS_ACK", nopHandler);
        handlers.put("CHANNEL_PINS_UPDATE", nopHandler);
        handlers.put("GUILD_INTEGRATIONS_UPDATE", nopHandler);
        handlers.put("PRESENCES_REPLACE", nopHandler);
        handlers.put("WEBHOOKS_UPDATE", nopHandler);
    }

    protected abstract class ConnectNode implements SessionController.SessionConnectNode {
        @NotNull
        JDA getJDA() {
            return api;
        }
    }

    protected class StartingNode extends ConnectNode {

        @Override
        public void run(boolean isLast) throws InterruptedException {
            if (shutdown)
                return;
            setupSendingThread();
            connect();
            if (isLast)
                return;
            try {
                api.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED);
            } catch (IllegalStateException ex) {
                close();
                LOG.debug("Shutdown while trying to connect");
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash("C", getJDA());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof StartingNode))
                return false;
            StartingNode node = (StartingNode) obj;
            return node.getJDA().equals(getJDA());
        }
    }

    protected class ReconnectNode extends ConnectNode {

        @Override
        public void run(boolean isLast) throws InterruptedException {
            if (shutdown)
                return;
            reconnect(true);
            if (isLast)
                return;
            try {
                api.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED);
            } catch (IllegalStateException ex) {
                close();
                LOG.debug("Shutdown while trying to reconnect");
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash("R", getJDA());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof ReconnectNode))
                return false;
            ReconnectNode node = (ReconnectNode) obj;
            return node.getJDA().equals(getJDA());
        }
    }
}
