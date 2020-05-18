package me.syari.ss.discord.internal;

import com.neovisionaries.ws.client.WebSocketFactory;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.MessageReceivedEvent;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.*;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.handle.GuildSetupController;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class JDA {
    @Contract(pure = true)
    public static @Nullable JDA build(@NotNull String token, Consumer<MessageReceivedEvent> messageReceivedEvent) throws LoginException {
        if (token.isEmpty()) {
            return null;
        } else {
            JDA jda = new JDA(token, messageReceivedEvent);
            jda.setStatus(JDA.Status.INITIALIZED);
            jda.login();
            return jda;
        }
    }

    protected final SnowflakeCacheView<User> userCache = new SnowflakeCacheView<>(User.class);
    protected final SnowflakeCacheView<Guild> guildCache = new SnowflakeCacheView<>(Guild.class);
    protected final SnowflakeCacheView<TextChannel> textChannelCache = new SnowflakeCacheView<>(TextChannel.class);

    protected final TLongObjectMap<User> fakeUsers = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), new Object());

    protected final Thread shutdownHook;
    protected final EntityBuilder entityBuilder = new EntityBuilder(this);
    protected final EventCache eventCache;

    protected final GuildSetupController guildSetupController;

    protected final String token;
    protected final ThreadingConfig threadConfig = new ThreadingConfig();
    protected final SessionConfig sessionConfig = new SessionConfig();
    private final Consumer<MessageReceivedEvent> messageReceivedEvent;

    protected WebSocketClient client;
    protected final Requester requester;
    protected Status status = Status.INITIALIZING;
    protected long responseTotal;
    protected String gatewayUrl;

    public JDA(@NotNull String token,
               @NotNull Consumer<MessageReceivedEvent> messageReceivedEvent) {
        this.token = "Bot " + token;
        this.messageReceivedEvent = messageReceivedEvent;
        this.shutdownHook = new Thread(this::shutdown, "JDA Shutdown Hook");
        this.requester = new Requester(this);
        this.requester.setRetryOnTimeout(true);
        this.guildSetupController = new GuildSetupController(this);
        this.eventCache = new EventCache();
    }

    public boolean chunkGuild(long id) {
        try {
            return ChunkingFilter.ALL.filter(id);
        } catch (Exception e) {
            return true;
        }
    }

    public SessionController getSessionController() {
        return sessionConfig.getSessionController();
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
        if (shutdownHook != null)
            Runtime.getRuntime().addShutdownHook(shutdownHook);

    }

    public String getGateway() {
        return getSessionController().getGateway(this);
    }

    public void setStatus(Status status) {
        synchronized (this.status) {
            this.status = status;
        }
    }

    public void verifyToken() throws LoginException {
        RestAction<DataObject> login = new RestAction<DataObject>(this, Route.GET_SELF.compile()) {
            @Override
            public void handleResponse(@NotNull Response response, Request<DataObject> request) {
                if (response.isOk()) {
                    request.onSuccess(response.getObject());
                } else if (response.isRateLimit()) {
                    request.onFailure(new RateLimitedException(request.getRoute(), response.retryAfter));
                } else if (response.code == 401) {
                    request.onSuccess(null);
                } else {
                    request.onFailure(new LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n" + response.toString()));
                }
            }
        };

        DataObject userResponse = checkToken(login);
        if (userResponse != null) {
            return;
        }

        userResponse = checkToken(login);
        shutdownNow();

        if (userResponse == null) {
            throw new LoginException("The provided token is invalid!");
        }
    }

    private DataObject checkToken(RestAction<DataObject> login) throws LoginException {
        DataObject userResponse;
        try {
            userResponse = login.complete();
        } catch (RuntimeException e) {
            Throwable ex = e.getCause() instanceof ExecutionException ? e.getCause().getCause() : null;
            if (ex instanceof LoginException)
                throw new LoginException(ex.getMessage());
            else
                throw e;
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
        if (!status.isInit()) {
            throw new IllegalArgumentException(String.format("Cannot await the status %s as it is not part of the login cycle!", status));
        }
        if (getStatus() == Status.CONNECTED) {
            return;
        }
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
        return sessionConfig.getHttpClient();
    }

    @NotNull
    public ISnowflakeCacheView<Guild> getGuildCache() {
        return guildCache;
    }

    @Nullable
    public Guild getGuildById(long id) {
        return getGuildCache().getElementById(id);
    }

    public boolean isUnavailable(long guildId) {
        return guildSetupController.isUnavailable(guildId);
    }

    @NotNull
    public ISnowflakeCacheView<Emote> getEmoteCache() {
        return CacheView.allSnowflakes(() -> guildCache.stream().map(Guild::getEmoteCache));
    }

    @Nullable
    public Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }

    @NotNull
    public ISnowflakeCacheView<TextChannel> getTextChannelCache() {
        return textChannelCache;
    }

    @Nullable
    public TextChannel getTextChannelById(long id) {
        return getTextChannelCache().getElementById(id);
    }

    @NotNull
    public ISnowflakeCacheView<User> getUserCache() {
        return userCache;
    }

    @Nullable
    public User getUserById(long id) {
        return getUserCache().getElementById(id);
    }

    private synchronized void shutdownNow() {
        shutdown();
        threadConfig.shutdownNow();
    }

    public synchronized void shutdown() {
        if (status == Status.SHUTDOWN || status == Status.SHUTTING_DOWN) {
            return;
        }

        setStatus(Status.SHUTTING_DOWN);

        WebSocketClient client = getClient();
        if (client != null) {
            client.shutdown();
        }

        shutdownInternals();
    }

    public void callMessageReceiveEvent(Message message) {
        messageReceivedEvent.accept(new MessageReceivedEvent(message));
    }

    public synchronized void shutdownInternals() {
        if (status == Status.SHUTDOWN) {
            return;
        }

        getRequester().shutdown();
        threadConfig.shutdown();

        if (shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
        return sessionConfig.getWebSocketFactory();
    }

    public WebSocketClient getClient() {
        return client;
    }

    public SnowflakeCacheView<User> getUsersView() {
        return userCache;
    }

    public SnowflakeCacheView<Guild> getGuildsView() {
        return guildCache;
    }

    public SnowflakeCacheView<TextChannel> getTextChannelsView() {
        return textChannelCache;
    }

    public TLongObjectMap<User> getFakeUserMap() {
        return fakeUsers;
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
