
package me.syari.ss.discord.api.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.audio.factory.DefaultSendFactory;
import me.syari.ss.discord.api.audio.factory.IAudioSendSystem;
import me.syari.ss.discord.api.events.RawGatewayEvent;
import me.syari.ss.discord.api.events.message.MessageBulkDeleteEvent;
import me.syari.ss.discord.api.hooks.*;
import me.syari.ss.discord.api.hooks.EventListener;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.utils.SessionControllerAdapter;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.audio.factory.IAudioSendFactory;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.Compression;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.config.flags.ConfigFlag;
import me.syari.ss.discord.internal.utils.config.flags.ShardingConfigFlag;
import me.syari.ss.discord.internal.utils.config.sharding.*;
import me.syari.ss.discord.internal.utils.config.sharding.*;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


public class  DefaultShardManagerBuilder
{
    protected final List<Object> listeners = new ArrayList<>();
    protected final List<IntFunction<Object>> listenerProviders = new ArrayList<>();
    protected SessionController sessionController = null;
    protected VoiceDispatchInterceptor voiceDispatchInterceptor = null;
    protected EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
    protected EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected EnumSet<ShardingConfigFlag> shardingFlags = ShardingConfigFlag.getDefault();
    protected Compression compression = Compression.ZLIB;
    protected int shardsTotal = -1;
    protected int maxReconnectDelay = 900;
    protected int largeThreshold = 250;
    protected int maxBufferSize = 2048;
    protected String token = null;
    protected IntFunction<Boolean> idleProvider = null;
    protected IntFunction<OnlineStatus> statusProvider = null;
    protected IntFunction<? extends Activity> activityProvider = null;
    protected IntFunction<? extends ConcurrentMap<String, String>> contextProvider = null;
    protected IntFunction<? extends IEventManager> eventManagerProvider = null;
    protected ThreadPoolProvider<? extends ScheduledExecutorService> rateLimitPoolProvider = null;
    protected ThreadPoolProvider<? extends ScheduledExecutorService> gatewayPoolProvider = null;
    protected ThreadPoolProvider<? extends ExecutorService> callbackPoolProvider = null;
    protected Collection<Integer> shards = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected OkHttpClient httpClient = null;
    protected WebSocketFactory wsFactory = null;
    protected IAudioSendFactory audioSendFactory = null;
    protected ThreadFactory threadFactory = null;
    protected ChunkingFilter chunkingFilter;

    
    public DefaultShardManagerBuilder() {}

    
    public DefaultShardManagerBuilder(@Nonnull String token)
    {
        this.setToken(token);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRawEventsEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.RAW_EVENTS, enable);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRelativeRateLimit(boolean enable)
    {
        return setFlag(ConfigFlag.USE_RELATIVE_RATELIMIT, enable);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setEnabledCacheFlags(@Nullable EnumSet<CacheFlag> flags)
    {
        this.cacheFlags = flags == null ? EnumSet.noneOf(CacheFlag.class) : EnumSet.copyOf(flags);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setDisabledCacheFlags(@Nullable EnumSet<CacheFlag> flags)
    {
        return setEnabledCacheFlags(flags == null ? EnumSet.allOf(CacheFlag.class) : EnumSet.complementOf(flags));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setSessionController(@Nullable SessionController controller)
    {
        this.sessionController = controller;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setVoiceDispatchInterceptor(@Nullable VoiceDispatchInterceptor interceptor)
    {
        this.voiceDispatchInterceptor = interceptor;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setContextMap(@Nullable IntFunction<? extends ConcurrentMap<String, String>> provider)
    {
        this.contextProvider = provider;
        if (provider != null)
            setContextEnabled(true);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setContextEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.MDC_CONTEXT, enable);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setCompression(@Nonnull Compression compression)
    {
        Checks.notNull(compression, "Compression");
        this.compression = compression;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder addEventListeners(@Nonnull final Object... listeners)
    {
        return this.addEventListeners(Arrays.asList(listeners));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder addEventListeners(@Nonnull final Collection<Object> listeners)
    {
        Checks.noneNull(listeners, "listeners");

        this.listeners.addAll(listeners);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder removeEventListeners(@Nonnull final Object... listeners)
    {
        return this.removeEventListeners(Arrays.asList(listeners));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder removeEventListeners(@Nonnull final Collection<Object> listeners)
    {
        Checks.noneNull(listeners, "listeners");

        this.listeners.removeAll(listeners);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder addEventListenerProvider(@Nonnull final IntFunction<Object> listenerProvider)
    {
        return this.addEventListenerProviders(Collections.singleton(listenerProvider));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder addEventListenerProviders(@Nonnull final Collection<IntFunction<Object>> listenerProviders)
    {
        Checks.noneNull(listenerProviders, "listener providers");

        this.listenerProviders.addAll(listenerProviders);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder removeEventListenerProvider(@Nonnull final IntFunction<Object> listenerProvider)
    {
        return this.removeEventListenerProviders(Collections.singleton(listenerProvider));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder removeEventListenerProviders(@Nonnull final Collection<IntFunction<Object>> listenerProviders)
    {
        Checks.noneNull(listenerProviders, "listener providers");

        this.listenerProviders.removeAll(listenerProviders);
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setAudioSendFactory(@Nullable final IAudioSendFactory factory)
    {
        this.audioSendFactory = factory;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setAutoReconnect(final boolean autoReconnect)
    {
        return setFlag(ConfigFlag.AUTO_RECONNECT, autoReconnect);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setBulkDeleteSplittingEnabled(final boolean enabled)
    {
        return setFlag(ConfigFlag.BULK_DELETE_SPLIT, enabled);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setEnableShutdownHook(final boolean enable)
    {
        return setFlag(ConfigFlag.SHUTDOWN_HOOK, enable);
    }

    
    @Nonnull
    @Deprecated
    @DeprecatedSince("3.8.1")
    @ReplaceWith("setEventManagerProvider((id) -> manager)")
    public DefaultShardManagerBuilder setEventManager(@Nonnull final IEventManager manager)
    {
        Checks.notNull(manager, "manager");

        return setEventManagerProvider((id) -> manager);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setEventManagerProvider(@Nonnull final IntFunction<? extends IEventManager> eventManagerProvider)
    {
        Checks.notNull(eventManagerProvider, "eventManagerProvider");
        this.eventManagerProvider = eventManagerProvider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setActivity(@Nullable final Activity activity)
    {
        return this.setActivityProvider(id -> activity);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setActivityProvider(@Nullable final IntFunction<? extends Activity> activityProvider)
    {
        this.activityProvider = activityProvider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setIdle(final boolean idle)
    {
        return this.setIdleProvider(id -> idle);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setIdleProvider(@Nullable final IntFunction<Boolean> idleProvider)
    {
        this.idleProvider = idleProvider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setStatus(@Nullable final OnlineStatus status)
    {
        Checks.notNull(status, "status");
        Checks.check(status != OnlineStatus.UNKNOWN, "OnlineStatus cannot be unknown!");

        return this.setStatusProvider(id -> status);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setStatusProvider(@Nullable final IntFunction<OnlineStatus> statusProvider)
    {
        this.statusProvider = statusProvider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setThreadFactory(@Nullable final ThreadFactory threadFactory)
    {
        this.threadFactory = threadFactory;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setHttpClientBuilder(@Nullable OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setHttpClient(@Nullable OkHttpClient client)
    {
        this.httpClient = client;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRateLimitPool(@Nullable ScheduledExecutorService pool)
    {
        return setRateLimitPool(pool, pool == null);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRateLimitPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        return setRateLimitPoolProvider(pool == null ? null : new ThreadPoolProviderImpl<>(pool, automaticShutdown));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRateLimitPoolProvider(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> provider)
    {
        this.rateLimitPoolProvider = provider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setGatewayPool(@Nullable ScheduledExecutorService pool)
    {
        return setGatewayPool(pool, pool == null);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setGatewayPool(@Nullable ScheduledExecutorService pool, boolean automaticShutdown)
    {
        return setGatewayPoolProvider(pool == null ? null : new ThreadPoolProviderImpl<>(pool, automaticShutdown));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setGatewayPoolProvider(@Nullable ThreadPoolProvider<? extends ScheduledExecutorService> provider)
    {
        this.gatewayPoolProvider = provider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setCallbackPool(@Nullable ExecutorService executor)
    {
        return setCallbackPool(executor, executor == null);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setCallbackPool(@Nullable ExecutorService executor, boolean automaticShutdown)
    {
        return setCallbackPoolProvider(executor == null ? null : new ThreadPoolProviderImpl<>(executor, automaticShutdown));
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setCallbackPoolProvider(@Nullable ThreadPoolProvider<? extends ExecutorService> provider)
    {
        this.callbackPoolProvider = provider;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setMaxReconnectDelay(final int maxReconnectDelay)
    {
        Checks.check(maxReconnectDelay >= 32, "Max reconnect delay must be 32 seconds or greater. You provided %d.", maxReconnectDelay);

        this.maxReconnectDelay = maxReconnectDelay;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setRequestTimeoutRetry(boolean retryOnTimeout)
    {
        return setFlag(ConfigFlag.RETRY_TIMEOUT, retryOnTimeout);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setShards(final int... shardIds)
    {
        Checks.notNull(shardIds, "shardIds");
        for (int id : shardIds)
        {
            Checks.notNegative(id, "minShardId");
            Checks.check(id < this.shardsTotal, "maxShardId must be lower than shardsTotal");
        }

        this.shards = Arrays.stream(shardIds).boxed().collect(Collectors.toSet());

        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setShards(final int minShardId, final int maxShardId)
    {
        Checks.notNegative(minShardId, "minShardId");
        Checks.check(maxShardId < this.shardsTotal, "maxShardId must be lower than shardsTotal");
        Checks.check(minShardId <= maxShardId, "minShardId must be lower than or equal to maxShardId");

        List<Integer> shards = new ArrayList<>(maxShardId - minShardId + 1);
        for (int i = minShardId; i <= maxShardId; i++)
            shards.add(i);

        this.shards = shards;

        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setShards(@Nonnull Collection<Integer> shardIds)
    {
        Checks.notNull(shardIds, "shardIds");
        for (Integer id : shardIds)
        {
            Checks.notNegative(id, "minShardId");
            Checks.check(id < this.shardsTotal, "maxShardId must be lower than shardsTotal");
        }

        this.shards = new ArrayList<>(shardIds);

        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setShardsTotal(final int shardsTotal)
    {
        Checks.check(shardsTotal == -1 || shardsTotal > 0, "shardsTotal must either be -1 or greater than 0");
        this.shardsTotal = shardsTotal;

        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setToken(@Nonnull final String token)
    {
        Checks.notBlank(token, "token");

        this.token = token;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setUseShutdownNow(final boolean useShutdownNow)
    {
        return setFlag(ShardingConfigFlag.SHUTDOWN_NOW, useShutdownNow);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setWebsocketFactory(@Nullable WebSocketFactory factory)
    {
        this.wsFactory = factory;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setChunkingFilter(@Nullable ChunkingFilter filter)
    {
        this.chunkingFilter = filter;
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setGuildSubscriptionsEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.GUILD_SUBSCRIPTIONS, enabled);
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setLargeThreshold(int threshold)
    {
        this.largeThreshold = Math.max(50, Math.min(250, threshold)); // enforce 50 <= t <= 250
        return this;
    }

    
    @Nonnull
    public DefaultShardManagerBuilder setMaxBufferSize(int bufferSize)
    {
        Checks.notNegative(bufferSize, "The buffer size");
        this.maxBufferSize = bufferSize;
        return this;
    }

    
    @Nonnull
    public ShardManager build() throws LoginException, IllegalArgumentException
    {
        boolean useShutdownNow = shardingFlags.contains(ShardingConfigFlag.SHUTDOWN_NOW);
        final ShardingConfig shardingConfig = new ShardingConfig(shardsTotal, useShutdownNow);
        final EventConfig eventConfig = new EventConfig(eventManagerProvider);
        listeners.forEach(eventConfig::addEventListener);
        listenerProviders.forEach(eventConfig::addEventListenerProvider);
        final PresenceProviderConfig presenceConfig = new PresenceProviderConfig();
        presenceConfig.setActivityProvider(activityProvider);
        presenceConfig.setStatusProvider(statusProvider);
        presenceConfig.setIdleProvider(idleProvider);
        final ThreadingProviderConfig threadingConfig = new ThreadingProviderConfig(rateLimitPoolProvider, gatewayPoolProvider, callbackPoolProvider, threadFactory);
        final ShardingSessionConfig sessionConfig = new ShardingSessionConfig(sessionController, voiceDispatchInterceptor, httpClient, httpClientBuilder, wsFactory, audioSendFactory, flags, shardingFlags, maxReconnectDelay, largeThreshold);
        final ShardingMetaConfig metaConfig = new ShardingMetaConfig(maxBufferSize, contextProvider, cacheFlags, flags, compression);
        final DefaultShardManager manager = new DefaultShardManager(this.token, this.shards, shardingConfig, eventConfig, presenceConfig, threadingConfig, sessionConfig, metaConfig, chunkingFilter);

        manager.login();

        return manager;
    }

    private DefaultShardManagerBuilder setFlag(ConfigFlag flag, boolean enable)
    {
        if (enable)
            this.flags.add(flag);
        else
            this.flags.remove(flag);
        return this;
    }

    private DefaultShardManagerBuilder setFlag(ShardingConfigFlag flag, boolean enable)
    {
        if (enable)
            this.shardingFlags.add(flag);
        else
            this.shardingFlags.remove(flag);
        return this;
    }

    //Avoid having multiple anonymous classes
    private static class ThreadPoolProviderImpl<T extends ExecutorService> implements ThreadPoolProvider<T>
    {
        private final boolean autoShutdown;
        private final T pool;

        public ThreadPoolProviderImpl(T pool, boolean autoShutdown)
        {
            this.autoShutdown = autoShutdown;
            this.pool = pool;
        }

        @Override
        public T provide(int shardId)
        {
            return pool;
        }

        @Override
        public boolean shouldShutdownAutomatically(int shardId)
        {
            return autoShutdown;
        }
    }
}
