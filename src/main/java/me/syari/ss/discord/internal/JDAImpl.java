package me.syari.ss.discord.internal;

import com.neovisionaries.ws.client.WebSocketFactory;
import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.InterfacedEventManager;
import me.syari.ss.discord.api.hooks.ListenerAdapter;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.handle.EventCache;
import me.syari.ss.discord.internal.handle.GuildSetupController;
import me.syari.ss.discord.internal.hooks.EventManagerProxy;
import me.syari.ss.discord.internal.managers.PresenceImpl;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.MDC;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class JDAImpl implements JDA {
    public static final Logger LOG = JDALogger.getLog(JDA.class);

    protected ScheduledThreadPoolExecutor audioLifeCyclePool;

    protected final SnowflakeCacheViewImpl<User> userCache = new SnowflakeCacheViewImpl<>(User.class, User::getName);
    protected final SnowflakeCacheViewImpl<Guild> guildCache = new SnowflakeCacheViewImpl<>(Guild.class, Guild::getName);
    protected final SnowflakeCacheViewImpl<TextChannel> textChannelCache = new SnowflakeCacheViewImpl<>(TextChannel.class, GuildChannel::getName);

    protected final TLongObjectMap<User> fakeUsers = MiscUtil.newLongMap();

    protected final PresenceImpl presence;
    protected final Thread shutdownHook;
    protected final EntityBuilder entityBuilder = new EntityBuilder(this);
    protected final EventCache eventCache;
    protected final EventManagerProxy eventManager = new EventManagerProxy(new InterfacedEventManager());

    protected final GuildSetupController guildSetupController;

    protected final AuthorizationConfig authConfig;
    protected final ThreadingConfig threadConfig;
    protected final SessionConfig sessionConfig;
    protected final MetaConfig metaConfig;

    protected WebSocketClient client;
    protected Requester requester;
    protected Status status = Status.INITIALIZING;
    protected ShardInfo shardInfo;
    protected long responseTotal;
    protected long gatewayPing = -1;
    protected String gatewayUrl;
    protected ChunkingFilter chunkingFilter;

    public JDAImpl(
            AuthorizationConfig authConfig, SessionConfig sessionConfig,
            ThreadingConfig threadConfig, MetaConfig metaConfig) {
        this.authConfig = authConfig;
        this.threadConfig = threadConfig == null ? ThreadingConfig.getDefault() : threadConfig;
        this.sessionConfig = sessionConfig == null ? SessionConfig.getDefault() : sessionConfig;
        this.metaConfig = metaConfig == null ? MetaConfig.getDefault() : metaConfig;
        this.shutdownHook = this.metaConfig.isUseShutdownHook() ? new Thread(this::shutdown, "JDA Shutdown Hook") : null;
        this.presence = new PresenceImpl(this);
        this.requester = new Requester(this);
        this.requester.setRetryOnTimeout(this.sessionConfig.isRetryOnTimeout());
        this.guildSetupController = new GuildSetupController(this);
        this.eventCache = new EventCache(isGuildSubscriptions());
    }

    public void handleEvent(@Nonnull MessageReceivedEvent event) {
        eventManager.handle(event);
    }

    public boolean isRelativeRateLimit() {
        return sessionConfig.isRelativeRateLimit();
    }

    public boolean isCacheFlagSet(CacheFlag flag) {
        return metaConfig.getCacheFlags().contains(flag);
    }

    public boolean isGuildSubscriptions() {
        return metaConfig.isGuildSubscriptions();
    }

    public int getLargeThreshold() {
        return sessionConfig.getLargeThreshold();
    }

    public int getMaxBufferSize() {
        return metaConfig.getMaxBufferSize();
    }

    public boolean chunkGuild(long id) {
        try {
            return isGuildSubscriptions() && chunkingFilter.filter(id);
        } catch (Exception e) {
            LOG.error("Uncaught exception from chunking filter", e);
            return true;
        }
    }

    public void setChunkingFilter(ChunkingFilter filter) {
        this.chunkingFilter = filter;
    }

    public SessionController getSessionController() {
        return sessionConfig.getSessionController();
    }

    public GuildSetupController getGuildSetupController() {
        return guildSetupController;
    }

    public void login(ShardInfo shardInfo, Compression compression, boolean validateToken) throws LoginException {
        login(null, shardInfo, compression, validateToken);
    }

    public void login(String gatewayUrl, ShardInfo shardInfo, Compression compression, boolean validateToken) throws LoginException {
        this.shardInfo = shardInfo;
        threadConfig.init(this::getIdentifierString);
        requester.getRateLimiter().init();
        this.gatewayUrl = gatewayUrl == null ? getGateway() : gatewayUrl;
        Checks.notNull(this.gatewayUrl, "Gateway URL");

        String token = authConfig.getToken();
        setStatus(Status.LOGGING_IN);
        if (token.isEmpty())
            throw new LoginException("Provided token was null or empty!");

        Map<String, String> previousContext = null;
        ConcurrentMap<String, String> contextMap = metaConfig.getMdcContextMap();
        if (contextMap != null) {
            if (shardInfo != null) {
                contextMap.put("jda.shard", shardInfo.getShardString());
                contextMap.put("jda.shard.id", String.valueOf(shardInfo.getShardId()));
                contextMap.put("jda.shard.total", String.valueOf(shardInfo.getShardTotal()));
            }
            // set MDC metadata for build thread
            previousContext = MDC.getCopyOfContextMap();
            contextMap.forEach(MDC::put);
        }
        if (validateToken) {
            verifyToken();
            LOG.info("Login Successful!");
        }

        client = new WebSocketClient(this, compression);
        // remove our MDC metadata when we exit our code
        if (previousContext != null)
            previousContext.forEach(MDC::put);

        if (shutdownHook != null)
            Runtime.getRuntime().addShutdownHook(shutdownHook);

    }

    public String getGateway() {
        return getSessionController().getGateway(this);
    }


    public ConcurrentMap<String, String> getContextMap() {
        return metaConfig.getMdcContextMap() == null ? null : new ConcurrentHashMap<>(metaConfig.getMdcContextMap());
    }

    public void setContext() {
        if (metaConfig.getMdcContextMap() != null)
            metaConfig.getMdcContextMap().forEach(MDC::put);
    }

    public void setStatus(Status status) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (this.status) {
            this.status = status;
        }
    }

    public void verifyToken() throws LoginException {
        this.verifyToken(false);
    }

    // @param alreadyFailed If has already been a failed attempt with the current configuration
    public void verifyToken(boolean alreadyFailed) throws LoginException {

        RestActionImpl<DataObject> login = new RestActionImpl<DataObject>(this, Route.Self.GET_SELF.compile()) {
            @Override
            public void handleResponse(Response response, Request<DataObject> request) {
                if (response.isOk())
                    request.onSuccess(response.getObject());
                else if (response.isRateLimit())
                    request.onFailure(new RateLimitedException(request.getRoute(), response.retryAfter));
                else if (response.code == 401)
                    request.onSuccess(null);
                else
                    request.onFailure(new LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n" +
                            response.toString()));
            }
        };

        DataObject userResponse;

        if (!alreadyFailed) {
            userResponse = checkToken(login);
            if (userResponse != null) {
                return;
            }
        }

        //If we received a null return for userResponse, then that means we hit a 401.
        // 401 occurs when we attempt to access the users/@me endpoint with the wrong token prefix.
        // e.g: If we use a Client token and prefix it with "Bot ", or use a bot token and don't prefix it.
        // It also occurs when we attempt to access the endpoint with an invalid token.
        //The code below already knows that something is wrong with the token. We want to determine if it is invalid
        // or if the developer attempted to login with a token using the wrong AccountType.

        //If we attempted to login as a Bot, remove the "Bot " prefix and set the Requester to be a client.

        userResponse = checkToken(login);
        shutdownNow();

        //If the response isn't null (thus it didn't 401) send it to the secondary verify method to determine
        // which account type the developer wrongly attempted to login as
        if (userResponse == null) {
            throw new LoginException("The provided token is invalid!");
        }

    }

    private DataObject checkToken(RestActionImpl<DataObject> login) throws LoginException {
        DataObject userResponse;
        try {
            userResponse = login.complete();
        } catch (RuntimeException e) {
            //We check if the LoginException is masked inside of a ExecutionException which is masked inside of the RuntimeException
            Throwable ex = e.getCause() instanceof ExecutionException ? e.getCause().getCause() : null;
            if (ex instanceof LoginException)
                throw new LoginException(ex.getMessage());
            else
                throw e;
        }
        return userResponse;
    }

    public AuthorizationConfig getAuthorizationConfig() {
        return authConfig;
    }

    @Nonnull
    public String getToken() {
        return authConfig.getToken();
    }


    public boolean isAutoReconnect() {
        return sessionConfig.isAutoReconnect();
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    @Override
    public void awaitStatus(@Nonnull Status status, @Nonnull Status... failOn) throws InterruptedException {
        Checks.notNull(status, "Status");
        Checks.check(status.isInit(), "Cannot await the status %s as it is not part of the login cycle!", status);
        if (getStatus() == Status.CONNECTED)
            return;
        List<Status> failStatus = Arrays.asList(failOn);
        while (!getStatus().isInit()                         // JDA might disconnect while starting
                || getStatus().ordinal() < status.ordinal()) // Wait until status is bypassed
        {
            if (getStatus() == Status.SHUTDOWN)
                throw new IllegalStateException("Was shutdown trying to await status");
            else if (failStatus.contains(getStatus()))
                return;
            Thread.sleep(50);
        }
    }

    @Nonnull
    public ScheduledExecutorService getRateLimitPool() {
        return threadConfig.getRateLimitPool();
    }

    @Nonnull
    public ScheduledExecutorService getGatewayPool() {
        return threadConfig.getGatewayPool();
    }

    @Nonnull
    public ExecutorService getCallbackPool() {
        return threadConfig.getCallbackPool();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions") // this can't really happen unless you pass bad configs
    public OkHttpClient getHttpClient() {
        return sessionConfig.getHttpClient();
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<Guild> getGuildCache() {
        return guildCache;
    }

    public boolean isUnavailable(long guildId) {
        return guildSetupController.isUnavailable(guildId);
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<Role> getRoleCache() {
        return CacheView.allSnowflakes(() -> guildCache.stream().map(Guild::getRoleCache));
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<Emote> getEmoteCache() {
        return CacheView.allSnowflakes(() -> guildCache.stream().map(Guild::getEmoteCache));
    }

    @Nonnull
    @Override
    public SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return textChannelCache;
    }


    @Nonnull
    @Override
    public SnowflakeCacheView<User> getUserCache() {
        return userCache;
    }

    private synchronized void shutdownNow() {
        shutdown();
        threadConfig.shutdownNow();
    }

    private synchronized void shutdown() {
        if (status == Status.SHUTDOWN || status == Status.SHUTTING_DOWN)
            return;

        setStatus(Status.SHUTTING_DOWN);

        WebSocketClient client = getClient();
        if (client != null)
            client.shutdown();

        shutdownInternals();
    }

    public synchronized void shutdownInternals() {
        if (status == Status.SHUTDOWN)
            return;
        //so we can shutdown from WebSocketClient properly
        guildSetupController.close();

        getRequester().shutdown();
        if (audioLifeCyclePool != null)
            audioLifeCyclePool.shutdownNow();
        threadConfig.shutdown();

        if (shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (Exception ignored) {
            }
        }

        setStatus(Status.SHUTDOWN);
    }

    public long getResponseTotal() {
        return responseTotal;
    }

    public int getMaxReconnectDelay() {
        return sessionConfig.getMaxReconnectDelay();
    }

    @Nonnull
    public ShardInfo getShardInfo() {
        return shardInfo == null ? ShardInfo.SINGLE : shardInfo;
    }

    @Nonnull
    public Presence getPresence() {
        return presence;
    }

    public void setEventManager(IEventManager eventManager) {
        this.eventManager.setSubject(eventManager);
    }

    public void setEventListener(@Nonnull ListenerAdapter listener) {
        eventManager.register(listener);
    }

    public EntityBuilder getEntityBuilder() {
        return entityBuilder;
    }

    public void setGatewayPing(long ping) {
        this.gatewayPing = ping;
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

    public SnowflakeCacheViewImpl<User> getUsersView() {
        return userCache;
    }

    public SnowflakeCacheViewImpl<Guild> getGuildsView() {
        return guildCache;
    }

    public SnowflakeCacheViewImpl<TextChannel> getTextChannelsView() {
        return textChannelCache;
    }

    public TLongObjectMap<User> getFakeUserMap() {
        return fakeUsers;
    }

    public void setResponseTotal(int responseTotal) {
        this.responseTotal = responseTotal;
    }

    public String getIdentifierString() {
        if (shardInfo != null)
            return "JDA " + shardInfo.getShardString();
        else
            return "JDA";
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

}
