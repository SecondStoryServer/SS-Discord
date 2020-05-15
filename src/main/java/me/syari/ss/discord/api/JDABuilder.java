
package me.syari.ss.discord.api;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.annotations.Incubating;
import me.syari.ss.discord.api.audio.factory.IAudioSendFactory;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.exceptions.AccountTypeException;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.VoiceDispatchInterceptor;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.SessionControllerAdapter;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.managers.PresenceImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;


public class JDABuilder
{
    protected final List<Object> listeners;
    protected final AccountType accountType;

    protected ScheduledExecutorService rateLimitPool = null;
    protected boolean shutdownRateLimitPool = true;
    protected ScheduledExecutorService mainWsPool = null;
    protected boolean shutdownMainWsPool = true;
    protected ExecutorService callbackPool = null;
    protected boolean shutdownCallbackPool = true;
    protected EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
    protected ConcurrentMap<String, String> contextMap = null;
    protected SessionController controller = null;
    protected VoiceDispatchInterceptor voiceDispatchInterceptor = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected OkHttpClient httpClient = null;
    protected WebSocketFactory wsFactory = null;
    protected String token = null;
    protected IEventManager eventManager = null;
    protected IAudioSendFactory audioSendFactory = null;
    protected JDA.ShardInfo shardInfo = null;
    protected Compression compression = Compression.ZLIB;
    protected Activity activity = null;
    protected OnlineStatus status = OnlineStatus.ONLINE;
    protected boolean idle = false;
    protected int maxReconnectDelay = 900;
    protected int largeThreshold = 250;
    protected int maxBufferSize = 2048;
    protected EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected ChunkingFilter chunkingFilter = ChunkingFilter.ALL;


    public JDABuilder()
    {
        this(AccountType.BOT);
    }


    public JDABuilder(@Nullable String token)
    {
        this();
        setToken(token);
    }


    @Incubating
    public JDABuilder(@Nonnull AccountType accountType)
    {
        Checks.notNull(accountType, "accountType");

        this.accountType = accountType;
        this.listeners = new LinkedList<>();
    }


    @Nonnull
    public JDABuilder setRawEventsEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.RAW_EVENTS, enable);
    }


    @Nonnull
    public JDABuilder setRelativeRateLimit(boolean enable)
    {
        return setFlag(ConfigFlag.USE_RELATIVE_RATELIMIT, enable);
    }


    @Nonnull
    public JDABuilder setEnabledCacheFlags(@Nullable EnumSet<CacheFlag> flags)
    {
        this.cacheFlags = flags == null ? EnumSet.noneOf(CacheFlag.class) : EnumSet.copyOf(flags);
        return this;
    }


    @Nonnull
    public JDABuilder setDisabledCacheFlags(@Nullable EnumSet<CacheFlag> flags)
    {
        return setEnabledCacheFlags(flags == null ? EnumSet.allOf(CacheFlag.class) : EnumSet.complementOf(flags));
    }


    @Nonnull
    public JDABuilder setContextMap(@Nullable ConcurrentMap<String, String> map)
    {
        this.contextMap = map;
        if (map != null)
            setContextEnabled(true);
        return this;
    }


    @Nonnull
    public JDABuilder setContextEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.MDC_CONTEXT, enable);
    }


    @Nonnull
    public JDABuilder setCompression(@Nonnull Compression compression)
    {
        Checks.notNull(compression, "Compression");
        this.compression = compression;
        return this;
    }


    @Nonnull
    public JDABuilder setRequestTimeoutRetry(boolean retryOnTimeout)
    {
        return setFlag(ConfigFlag.RETRY_TIMEOUT, retryOnTimeout);
    }


    @Nonnull
    public JDABuilder setToken(@Nullable String token)
    {
        this.token = token;
        return this;
    }


    @Nonnull
    public JDABuilder setHttpClientBuilder(@Nullable OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }


    @Nonnull
    public JDABuilder setHttpClient(@Nullable OkHttpClient client)
    {
        this.httpClient = client;
        return this;
    }


    @Nonnull
    public JDABuilder setWebsocketFactory(@Nullable WebSocketFactory factory)
    {
        this.wsFactory = factory;
        return this;
    }


    @Nonnull
    public JDABuilder setRateLimitPool(@Nullable ScheduledExecutorService pool)
    {
        return setRateLimitPool(pool, pool == null);
    }


    @Nonnull
    public JDABuilder setRateLimitPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.rateLimitPool = pool;
        this.shutdownRateLimitPool = automaticShutdown;
        return this;
    }


    @Nonnull
    public JDABuilder setGatewayPool(@Nullable ScheduledExecutorService pool)
    {
        return setGatewayPool(pool, pool == null);
    }


    @Nonnull
    public JDABuilder setGatewayPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.mainWsPool = pool;
        this.shutdownMainWsPool = automaticShutdown;
        return this;
    }


    @Nonnull
    public JDABuilder setCallbackPool(@Nullable ExecutorService executor)
    {
        return setCallbackPool(executor, executor == null);
    }


    @Nonnull
    public JDABuilder setCallbackPool(@Nullable ExecutorService executor, boolean automaticShutdown)
    {
        this.callbackPool = executor;
        this.shutdownCallbackPool = automaticShutdown;
        return this;
    }


    @Nonnull
    public JDABuilder setBulkDeleteSplittingEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.BULK_DELETE_SPLIT, enabled);
    }


    @Nonnull
    public JDABuilder setEnableShutdownHook(boolean enable)
    {
        return setFlag(ConfigFlag.SHUTDOWN_HOOK, enable);
    }


    @Nonnull
    public JDABuilder setAutoReconnect(boolean autoReconnect)
    {
        return setFlag(ConfigFlag.AUTO_RECONNECT, autoReconnect);
    }


    @Nonnull
    public JDABuilder setEventManager(@Nullable IEventManager manager)
    {
        this.eventManager = manager;
        return this;
    }


    @Nonnull
    public JDABuilder setAudioSendFactory(@Nullable IAudioSendFactory factory)
    {
        this.audioSendFactory = factory;
        return this;
    }


    @Nonnull
    public JDABuilder setIdle(boolean idle)
    {
        this.idle = idle;
        return this;
    }


    @Nonnull
    public JDABuilder setActivity(@Nullable Activity activity)
    {
        this.activity = activity;
        return this;
    }


    @Nonnull
    @SuppressWarnings("ConstantConditions") // we have to enforce the nonnull at runtime
    public JDABuilder setStatus(@Nonnull OnlineStatus status)
    {
        if (status == null || status == OnlineStatus.UNKNOWN)
            throw new IllegalArgumentException("OnlineStatus cannot be null or unknown!");
        this.status = status;
        return this;
    }


    @Nonnull
    public JDABuilder addEventListeners(@Nonnull Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        Collections.addAll(this.listeners, listeners);
        return this;
    }


    @Nonnull
    public JDABuilder removeEventListeners(@Nonnull Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        this.listeners.removeAll(Arrays.asList(listeners));
        return this;
    }


    @Nonnull
    public JDABuilder setMaxReconnectDelay(int maxReconnectDelay)
    {
        Checks.check(maxReconnectDelay >= 32, "Max reconnect delay must be 32 seconds or greater. You provided %d.", maxReconnectDelay);

        this.maxReconnectDelay = maxReconnectDelay;
        return this;
    }


    @Nonnull
    public JDABuilder useSharding(int shardId, int shardTotal)
    {
        AccountTypeException.check(accountType, AccountType.BOT);
        Checks.notNegative(shardId, "Shard ID");
        Checks.positive(shardTotal, "Shard Total");
        Checks.check(shardId < shardTotal,
                "The shard ID must be lower than the shardTotal! Shard IDs are 0-based.");
        shardInfo = new JDA.ShardInfo(shardId, shardTotal);
        return this;
    }


    @Nonnull
    public JDABuilder setSessionController(@Nullable SessionController controller)
    {
        this.controller = controller;
        return this;
    }


    @Nonnull
    public JDABuilder setVoiceDispatchInterceptor(@Nullable VoiceDispatchInterceptor interceptor)
    {
        this.voiceDispatchInterceptor = interceptor;
        return this;
    }


    @Nonnull
    public JDABuilder setChunkingFilter(@Nullable ChunkingFilter filter)
    {
        this.chunkingFilter = filter == null ? ChunkingFilter.ALL : filter;
        return this;
    }


    @Nonnull
    public JDABuilder setGuildSubscriptionsEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.GUILD_SUBSCRIPTIONS, enabled);
    }


    @Nonnull
    public JDABuilder setLargeThreshold(int threshold)
    {
        this.largeThreshold = Math.max(50, Math.min(250, threshold)); // enforce 50 <= t <= 250
        return this;
    }


    @Nonnull
    public JDABuilder setMaxBufferSize(int bufferSize)
    {
        Checks.notNegative(bufferSize, "The buffer size");
        this.maxBufferSize = bufferSize;
        return this;
    }


    @Nonnull
    public JDA build() throws LoginException
    {
        OkHttpClient httpClient = this.httpClient;
        if (httpClient == null)
        {
            if (this.httpClientBuilder == null)
                this.httpClientBuilder = new OkHttpClient.Builder();
            httpClient = this.httpClientBuilder.build();
        }

        WebSocketFactory wsFactory = this.wsFactory == null ? new WebSocketFactory() : this.wsFactory;

        if (controller == null && shardInfo != null)
            controller = new SessionControllerAdapter();

        AuthorizationConfig authConfig = new AuthorizationConfig(accountType, token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
        threadingConfig.setGatewayPool(mainWsPool, shutdownMainWsPool);
        threadingConfig.setRateLimitPool(rateLimitPool, shutdownRateLimitPool);
        SessionConfig sessionConfig = new SessionConfig(controller, httpClient, wsFactory, voiceDispatchInterceptor, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, contextMap, cacheFlags, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig);
        jda.setChunkingFilter(chunkingFilter);

        if (eventManager != null)
            jda.setEventManager(eventManager);

        if (audioSendFactory != null)
            jda.setAudioSendFactory(audioSendFactory);

        listeners.forEach(jda::addEventListener);
        jda.setStatus(JDA.Status.INITIALIZED);  //This is already set by JDA internally, but this is to make sure the listeners catch it.

        // Set the presence information before connecting to have the correct information ready when sending IDENTIFY
        ((PresenceImpl) jda.getPresence())
                .setCacheActivity(activity)
                .setCacheIdle(idle)
                .setCacheStatus(status);
        jda.login(shardInfo, compression, true);
        return jda;
    }

    private JDABuilder setFlag(ConfigFlag flag, boolean enable)
    {
        if (enable)
            this.flags.add(flag);
        else
            this.flags.remove(flag);
        return this;
    }
}
