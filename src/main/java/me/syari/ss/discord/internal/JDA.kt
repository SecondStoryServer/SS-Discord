package me.syari.ss.discord.internal;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.MessageReceivedEvent;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.handle.GuildSetupController;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class JDA {
    @Contract(pure = true)
    public static @NotNull
    JDA build(@NotNull String token, Consumer<MessageReceivedEvent> messageReceivedEvent) throws LoginException {
        JDA jda = new JDA(token, messageReceivedEvent);
        jda.setStatus(JDA.Status.INITIALIZED);
        jda.login();
        return jda;
    }

    protected final Thread shutdownHook = new Thread(this::shutdown, "JDA Shutdown Hook");
    protected final EntityBuilder entityBuilder = new EntityBuilder(this);
    protected final EventCache eventCache = new EventCache();
    protected final GuildSetupController guildSetupController = new GuildSetupController(this);
    protected final String token;
    protected final ThreadingConfig threadConfig = new ThreadingConfig();
    private final SessionController sessionController = new SessionController();
    private final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private final WebSocketFactory webSocketFactory = new WebSocketFactory();
    private final Consumer<MessageReceivedEvent> messageReceivedEvent;
    protected WebSocketClient client;
    protected final Requester requester = new Requester(this);
    protected Status status = Status.INITIALIZING;
    protected long responseTotal;
    protected String gatewayUrl;

    public JDA(@NotNull String token, @NotNull Consumer<MessageReceivedEvent> messageReceivedEvent) {
        this.token = "Bot " + token;
        this.messageReceivedEvent = messageReceivedEvent;
    }

    public boolean chunkGuild(long id) {
        try {
            return ChunkingFilter.ALL.filter(id);
        } catch (Exception e) {
            return true;
        }
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public GuildSetupController getGuildSetupController() {
        return guildSetupController;
    }

    public void login() throws LoginException {
        threadConfig.init(() -> "JDA");
        requester.getRateLimiter().init();
        this.gatewayUrl = getGateway();
        setStatus(Status.LOGGING_IN);
        verifyToken();
        client = new WebSocketClient(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public String getGateway() {
        return sessionController.getGateway(this);
    }

    public void setStatus(Status status) {
        synchronized (this.status) {
            this.status = status;
        }
    }

    public void verifyToken() throws LoginException {
        RestAction<DataObject> login = new RestAction<DataObject>(this, Route.getSelfRoute()) {
            @Override
            public void handleResponse(@NotNull Response response, Request<DataObject> request) {
                if (response.isOk()) {
                    request.onSuccess(response.getDataObject());
                } else if (response.isRateLimit()) {
                    request.onFailure(new RateLimitedException(request.getRoute(), response.getRetryAfter()));
                } else if (response.getCode() == 401) {
                    request.onSuccess(null);
                } else {
                    request.onFailure(new LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n" + response.toString()));
                }
            }
        };
        DataObject userResponse = checkToken(login);
        if (userResponse != null) return;
        userResponse = checkToken(login);
        shutdownNow();
        if (userResponse == null) throw new LoginException("The provided token is invalid!");
    }

    private DataObject checkToken(RestAction<DataObject> login) throws LoginException {
        DataObject userResponse;
        try {
            userResponse = login.complete();
        } catch (RuntimeException ex) {
            Throwable throwable = ex.getCause() instanceof ExecutionException ? ex.getCause().getCause() : null;
            if (throwable instanceof LoginException) {
                throw new LoginException(throwable.getMessage());
            } else {
                throw ex;
            }
        }
        return userResponse;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    public void awaitStatus(@NotNull Status status, @NotNull Status... failOn) throws InterruptedException {
        if (!status.isInit())
            throw new IllegalArgumentException(String.format("Cannot await the status %s as it is not part of the login cycle!", status));
        if (getStatus() == Status.CONNECTED) return;
        List<Status> failStatus = Arrays.asList(failOn);
        while (!getStatus().isInit() || getStatus().ordinal() < status.ordinal()) {
            if (getStatus() == Status.SHUTDOWN) {
                throw new IllegalStateException("Was shutdown trying to await status");
            } else if (failStatus.contains(getStatus())) {
                return;
            } else {
                Thread.sleep(50);
            }
        }
    }

    public void awaitReady() throws InterruptedException {
        awaitStatus(Status.CONNECTED);
    }

    @NotNull
    public ScheduledExecutorService getRateLimitPool() {
        return threadConfig.getRateLimitPool();
    }

    @NotNull
    public ScheduledExecutorService getGatewayPool() {
        return threadConfig.getGatewayPool();
    }

    @NotNull
    public ExecutorService getCallbackPool() {
        return threadConfig.getCallbackPool();
    }

    @NotNull
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public boolean isUnavailable(long guildId) {
        return guildSetupController.isUnavailable(guildId);
    }

    private synchronized void shutdownNow() {
        shutdown();
        threadConfig.shutdownNow();
    }

    public synchronized void shutdown() {
        if (status == Status.SHUTDOWN || status == Status.SHUTTING_DOWN) return;
        setStatus(Status.SHUTTING_DOWN);
        WebSocketClient client = getClient();
        if (client != null) client.shutdown();
        shutdownInternals();
    }

    public void callMessageReceiveEvent(Message message) {
        messageReceivedEvent.accept(new MessageReceivedEvent(message));
    }

    public synchronized void shutdownInternals() {
        if (status == Status.SHUTDOWN) return;
        getRequester().shutdown();
        threadConfig.shutdown();
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setStatus(Status.SHUTDOWN);
    }

    public long getResponseTotal() {
        return responseTotal;
    }

    public EntityBuilder getEntityBuilder() {
        return entityBuilder;
    }

    public Requester getRequester() {
        return requester;
    }

    public WebSocketFactory getWebSocketFactory() {
        return webSocketFactory;
    }

    public WebSocketClient getClient() {
        return client;
    }

    public void setResponseTotal(int responseTotal) {
        this.responseTotal = responseTotal;
    }

    public EventCache getEventCache() {
        return eventCache;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void resetGatewayUrl() {
        this.gatewayUrl = getGateway();
    }

    public enum Status {
        INITIALIZING(true),
        INITIALIZED(true),
        LOGGING_IN(true),
        CONNECTING_TO_WEBSOCKET(true),
        IDENTIFYING_SESSION(true),
        AWAITING_LOGIN_CONFIRMATION(true),
        LOADING_SUBSYSTEMS(true),
        CONNECTED(true),
        DISCONNECTED(false),
        RECONNECT_QUEUED(false),
        WAITING_TO_RECONNECT(false),
        ATTEMPTING_TO_RECONNECT(false),
        SHUTTING_DOWN(false),
        SHUTDOWN(false);

        private final boolean isInit;

        Status(boolean isInit) {
            this.isInit = isInit;
        }

        public boolean isInit() {
            return isInit;
        }
    }

    @FunctionalInterface
    private interface ChunkingFilter {
        ChunkingFilter ALL = (x) -> true;

        boolean filter(long guildId);
    }
}
