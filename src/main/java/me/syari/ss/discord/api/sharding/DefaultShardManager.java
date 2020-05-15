
package me.syari.ss.discord.api.sharding;

import gnu.trove.set.TIntSet;
import me.syari.ss.discord.api.AccountType;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.utils.ChunkingFilter;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.cache.ShardCacheView;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.managers.PresenceImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.utils.UnlockHook;
import me.syari.ss.discord.internal.utils.cache.ShardCacheViewImpl;
import me.syari.ss.discord.internal.utils.config.AuthorizationConfig;
import me.syari.ss.discord.internal.utils.config.MetaConfig;
import me.syari.ss.discord.internal.utils.config.SessionConfig;
import me.syari.ss.discord.internal.utils.config.ThreadingConfig;
import me.syari.ss.discord.internal.utils.config.sharding.*;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;


public class DefaultShardManager implements ShardManager
{
    public static final Logger LOG = JDALogger.getLog(ShardManager.class);
    public static final ThreadFactory DEFAULT_THREAD_FACTORY = r ->
    {
        final Thread t = new Thread(r, "DefaultShardManager");
        t.setPriority(Thread.NORM_PRIORITY + 1);
        return t;
    };


    protected final ScheduledExecutorService executor;


    protected final Queue<Integer> queue = new ConcurrentLinkedQueue<>();


    protected ShardCacheViewImpl shards;


    protected final AtomicBoolean shutdown = new AtomicBoolean(false);


    protected final Thread shutdownHook;


    protected final String token;


    protected Future<?> worker;


    protected String gatewayURL;


    protected final PresenceProviderConfig presenceConfig;


    protected final EventConfig eventConfig;


    protected final ShardingConfig shardingConfig;


    protected final ThreadingProviderConfig threadingConfig;


    protected final ShardingSessionConfig sessionConfig;


    protected final ShardingMetaConfig metaConfig;


    protected final ChunkingFilter chunkingFilter;

    public DefaultShardManager(@Nonnull String token)
    {
        this(token, null);
    }

    public DefaultShardManager(@Nonnull String token, @Nullable Collection<Integer> shardIds)
    {
        this(token, shardIds, null, null, null, null, null, null, null);
    }

    public DefaultShardManager(
        @Nonnull String token, @Nullable Collection<Integer> shardIds,
        @Nullable ShardingConfig shardingConfig, @Nullable EventConfig eventConfig,
        @Nullable PresenceProviderConfig presenceConfig, @Nullable ThreadingProviderConfig threadingConfig,
        @Nullable ShardingSessionConfig sessionConfig, @Nullable ShardingMetaConfig metaConfig,
        @Nullable ChunkingFilter chunkingFilter)
    {
        this.token = token;
        this.eventConfig = eventConfig == null ? EventConfig.getDefault() : eventConfig;
        this.shardingConfig = shardingConfig == null ? ShardingConfig.getDefault() : shardingConfig;
        this.threadingConfig = threadingConfig == null ? ThreadingProviderConfig.getDefault() : threadingConfig;
        this.sessionConfig = sessionConfig == null ? ShardingSessionConfig.getDefault() : sessionConfig;
        this.presenceConfig = presenceConfig == null ? PresenceProviderConfig.getDefault() : presenceConfig;
        this.metaConfig = metaConfig == null ? ShardingMetaConfig.getDefault() : metaConfig;
        this.chunkingFilter = chunkingFilter == null ? ChunkingFilter.ALL : chunkingFilter;
        this.executor = createExecutor(this.threadingConfig.getThreadFactory());
        this.shutdownHook = this.metaConfig.isUseShutdownHook() ? new Thread(this::shutdown, "JDA Shutdown Hook") : null;

        synchronized (queue)
        {
            if (getShardsTotal() != -1)
            {
                if (shardIds == null)
                {
                    this.shards = new ShardCacheViewImpl(getShardsTotal());
                    for (int i = 0; i < getShardsTotal(); i++)
                        this.queue.add(i);
                }
                else
                {
                    this.shards = new ShardCacheViewImpl(shardIds.size());
                    shardIds.stream().distinct().sorted().forEach(this.queue::add);
                }
            }
        }
    }

    @Override
    public void addEventListener(@Nonnull final Object... listeners)
    {
        ShardManager.super.addEventListener(listeners);
        for (Object o : listeners)
            eventConfig.addEventListener(o);
    }

    @Override
    public void removeEventListener(@Nonnull final Object... listeners)
    {
        ShardManager.super.removeEventListener(listeners);
        for (Object o : listeners)
            eventConfig.removeEventListener(o);
    }

    @Override
    public void addEventListeners(@Nonnull IntFunction<Object> eventListenerProvider)
    {
        ShardManager.super.addEventListeners(eventListenerProvider);
        eventConfig.addEventListenerProvider(eventListenerProvider);
    }

    @Override
    public void removeEventListenerProvider(@Nonnull IntFunction<Object> eventListenerProvider)
    {
        eventConfig.removeEventListenerProvider(eventListenerProvider);
    }

    @Override
    public int getShardsQueued()
    {
        return this.queue.size();
    }

    @Override
    public int getShardsTotal()
    {
        return shardingConfig.getShardsTotal();
    }

    @Override
    public Guild getGuildById(long id)
    {
        int shardId = MiscUtil.getShardForGuild(id, getShardsTotal());
        JDA shard = this.getShardById(shardId);
        return shard == null ? null : shard.getGuildById(id);
    }

    @Nonnull
    @Override
    public ShardCacheView getShardCache()
    {
        return this.shards;
    }

    public void login() throws LoginException
    {
        // building the first one in the current thread ensures that LoginException and IllegalArgumentException can be thrown on login
        JDAImpl jda = null;
        try
        {
            final int shardId = this.queue.isEmpty() ? 0 : this.queue.peek();

            jda = this.buildInstance(shardId);
            try (UnlockHook hook = this.shards.writeLock())
            {
                this.shards.getMap().put(shardId, jda);
            }
            synchronized (queue)
            {
                this.queue.remove(shardId);
            }
        }
        catch (final InterruptedException e)
        {
            LOG.error("Interrupted Startup", e);
            throw new IllegalStateException(e);
        }
        catch (final Exception e)
        {
            if (jda != null)
            {
                if (shardingConfig.isUseShutdownNow())
                    jda.shutdownNow();
                else
                    jda.shutdown();
            }

            throw e;
        }

        runQueueWorker();
        //this.worker = this.executor.scheduleWithFixedDelay(this::processQueue, 5000, 5000, TimeUnit.MILLISECONDS); // 5s for ratelimit

        if (this.shutdownHook != null)
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    @Override
    public void restart(final int shardId)
    {
        Checks.notNegative(shardId, "shardId");
        Checks.check(shardId < getShardsTotal(), "shardId must be lower than shardsTotal");

        try (UnlockHook hook = this.shards.writeLock())
        {
            final JDA jda = this.shards.getMap().remove(shardId);
            if (jda != null)
            {
                if (shardingConfig.isUseShutdownNow())
                    jda.shutdownNow();
                else
                    jda.shutdown();
            }
        }

        enqueueShard(shardId);
    }

    @Override
    public void restart()
    {
        TIntSet map = this.shards.keySet();

        Arrays.stream(map.toArray())
              .sorted() // this ensures shards are started in natural order
              .forEach(this::restart);
    }

    @Override
    public void shutdown()
    {
        if (this.shutdown.getAndSet(true))
            return; // shutdown has already been requested

        if (this.worker != null && !this.worker.isDone())
            this.worker.cancel(true);

        if (this.shutdownHook != null)
        {
            try
            {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            }
            catch (final Exception ignored) {}
        }

        this.executor.shutdown();

        if (this.shards != null)
        {
            this.shards.forEach(jda ->
            {
                if (shardingConfig.isUseShutdownNow())
                    jda.shutdownNow();
                else
                    jda.shutdown();
            });
        }
    }

    @Override
    public void shutdown(final int shardId)
    {
        try (UnlockHook hook = this.shards.writeLock())
        {
            final JDA jda = this.shards.getMap().remove(shardId);
            if (jda != null)
            {
                if (shardingConfig.isUseShutdownNow())
                    jda.shutdownNow();
                else
                    jda.shutdown();
            }
        }
    }

    @Override
    public void start(final int shardId)
    {
        Checks.notNegative(shardId, "shardId");
        Checks.check(shardId < getShardsTotal(), "shardId must be lower than shardsTotal");
        enqueueShard(shardId);
    }

    protected void enqueueShard(final int shardId)
    {
        synchronized (queue)
        {
            queue.add(shardId);
            runQueueWorker();
        }
    }

    protected void runQueueWorker()
    {
        if (worker != null)
            return;
        worker = executor.submit(() ->
        {
            while (!queue.isEmpty())
                processQueue();
            this.gatewayURL = null;
            synchronized (queue)
            {
                worker = null;
                if (!shutdown.get() && !queue.isEmpty())
                    runQueueWorker();
            }
        });
    }

    protected void processQueue()
    {
        int shardId;

        if (this.shards == null)
        {
            shardId = 0;
        }
        else
        {
            Integer tmp = this.queue.peek();

            shardId = tmp == null ? -1 : tmp;
        }

        if (shardId == -1)
            return;

        JDAImpl api;
        try
        {
            api = this.shards == null ? null : (JDAImpl) this.shards.getElementById(shardId);

            if (api == null)
                api = this.buildInstance(shardId);
        }
        catch (InterruptedException e)
        {
            //caused by shutdown
            LOG.debug("Queue has been interrupted", e);
            return;
        }
        catch (LoginException e)
        {
            // this can only happen if the token has been changed
            // in this case the ShardManager will just shutdown itself as there currently is no way of hot-swapping the token on a running JDA instance.
            LOG.warn("The token has been invalidated and the ShardManager will shutdown!", e);
            this.shutdown();
            return;
        }
        catch (final Exception e)
        {
            LOG.error("Caught an exception in the queue processing thread", e);
            return;
        }

        try (UnlockHook hook = this.shards.writeLock())
        {
            this.shards.getMap().put(shardId, api);
        }
        synchronized (queue)
        {
            this.queue.remove(shardId);
        }
    }

    protected JDAImpl buildInstance(final int shardId) throws LoginException, InterruptedException
    {
        OkHttpClient httpClient = sessionConfig.getHttpClient();
        if (httpClient == null)
        {
            //httpClient == null implies we have a builder
            //noinspection ConstantConditions
            httpClient = sessionConfig.getHttpBuilder().build();
        }

        // imagine if we had macros or closures or destructuring :)
        ExecutorPair<ScheduledExecutorService> rateLimitPair = resolveExecutor(threadingConfig.getRateLimitPoolProvider(), shardId);
        ScheduledExecutorService rateLimitPool = rateLimitPair.executor;
        boolean shutdownRateLimitPool = rateLimitPair.automaticShutdown;

        ExecutorPair<ScheduledExecutorService> gatewayPair = resolveExecutor(threadingConfig.getGatewayPoolProvider(), shardId);
        ScheduledExecutorService gatewayPool = gatewayPair.executor;
        boolean shutdownGatewayPool = gatewayPair.automaticShutdown;

        ExecutorPair<ExecutorService> callbackPair = resolveExecutor(threadingConfig.getCallbackPoolProvider(), shardId);
        ExecutorService callbackPool = callbackPair.executor;
        boolean shutdownCallbackPool = callbackPair.automaticShutdown;

        AuthorizationConfig authConfig = new AuthorizationConfig(AccountType.BOT, token);
        SessionConfig sessionConfig = this.sessionConfig.toSessionConfig(httpClient);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setRateLimitPool(rateLimitPool, shutdownRateLimitPool);
        threadingConfig.setGatewayPool(gatewayPool, shutdownGatewayPool);
        threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
        MetaConfig metaConfig = new MetaConfig(this.metaConfig.getMaxBufferSize(), this.metaConfig.getContextMap(shardId), this.metaConfig.getCacheFlags(), this.sessionConfig.getFlags());
        final JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig);
        jda.setChunkingFilter(chunkingFilter);
        threadingConfig.init(jda::getIdentifierString);

        jda.setShardManager(this);

        if (eventConfig.getEventManagerProvider() != null)
            jda.setEventManager(this.eventConfig.getEventManagerProvider().apply(shardId));

        if (this.sessionConfig.getAudioSendFactory() != null)
            jda.setAudioSendFactory(this.sessionConfig.getAudioSendFactory());

        this.eventConfig.getListeners().forEach(jda::addEventListener);
        this.eventConfig.getListenerProviders().forEach(provider -> jda.addEventListener(provider.apply(shardId)));
        jda.setStatus(JDA.Status.INITIALIZED); //This is already set by JDA internally, but this is to make sure the listeners catch it.

        // Set the presence information before connecting to have the correct information ready when sending IDENTIFY
        PresenceImpl presence = ((PresenceImpl) jda.getPresence());
        if (presenceConfig.getActivityProvider() != null)
            presence.setCacheActivity(presenceConfig.getActivityProvider().apply(shardId));
        if (presenceConfig.getIdleProvider() != null)
            presence.setCacheIdle(presenceConfig.getIdleProvider().apply(shardId));
        if (presenceConfig.getStatusProvider() != null)
            presence.setCacheStatus(presenceConfig.getStatusProvider().apply(shardId));

        if (this.gatewayURL == null)
        {
            try
            {
                SessionController.ShardedGateway gateway = jda.getShardedGateway();
                this.gatewayURL = gateway.getUrl();
                if (this.gatewayURL == null)
                    LOG.error("Acquired null gateway url from SessionController");
                else
                    LOG.info("Login Successful!");

                if (getShardsTotal() == -1)
                {
                    shardingConfig.setShardsTotal(gateway.getShardTotal());
                    this.shards = new ShardCacheViewImpl(getShardsTotal());

                    synchronized (queue)
                    {
                        for (int i = 0; i < getShardsTotal(); i++)
                            queue.add(i);
                    }
                }
            }
            catch (RuntimeException e)
            {
                if (e.getCause() instanceof InterruptedException)
                    throw (InterruptedException) e.getCause();
                //We check if the LoginException is masked inside of a ExecutionException which is masked inside of the RuntimeException
                Throwable ex = e.getCause() instanceof ExecutionException ? e.getCause().getCause() : null;
                if (ex instanceof LoginException)
                    throw new LoginException(ex.getMessage());
                else
                    throw e;
            }
        }

        final JDA.ShardInfo shardInfo = new JDA.ShardInfo(shardId, getShardsTotal());

        final int shardTotal = jda.login(this.gatewayURL, shardInfo, this.metaConfig.getCompression(), false);
        if (getShardsTotal() == -1)
            shardingConfig.setShardsTotal(shardTotal);

        return jda;
    }

    @Override
    public void setActivityProvider(IntFunction<? extends Activity> activityProvider)
    {
        ShardManager.super.setActivityProvider(activityProvider);
        presenceConfig.setActivityProvider(activityProvider);
    }

    @Override
    public void setIdleProvider(@Nonnull IntFunction<Boolean> idleProvider)
    {
        ShardManager.super.setIdleProvider(idleProvider);
        presenceConfig.setIdleProvider(idleProvider);
    }

    @Override
    public void setPresenceProvider(IntFunction<OnlineStatus> statusProvider, IntFunction<? extends Activity> activityProvider)
    {
        ShardManager.super.setPresenceProvider(statusProvider, activityProvider);
        presenceConfig.setStatusProvider(statusProvider);
        presenceConfig.setActivityProvider(activityProvider);
    }

    @Override
    public void setStatusProvider(IntFunction<OnlineStatus> statusProvider)
    {
        ShardManager.super.setStatusProvider(statusProvider);
        presenceConfig.setStatusProvider(statusProvider);
    }


    protected ScheduledExecutorService createExecutor(ThreadFactory threadFactory)
    {
        ThreadFactory factory = threadFactory == null
            ? DEFAULT_THREAD_FACTORY
            : threadFactory;

        return Executors.newSingleThreadScheduledExecutor(factory);
    }

    protected static <E extends ExecutorService> ExecutorPair<E> resolveExecutor(ThreadPoolProvider<? extends E> provider, int shardId)
    {
        E executor = null;
        boolean automaticShutdown = true;
        if (provider != null)
        {
            executor = provider.provide(shardId);
            automaticShutdown = provider.shouldShutdownAutomatically(shardId);
        }
        return new ExecutorPair<>(executor, automaticShutdown);
    }

    protected static class ExecutorPair<E extends ExecutorService>
    {
        protected final E executor;
        protected final boolean automaticShutdown;

        protected ExecutorPair(E executor, boolean automaticShutdown)
        {
            this.executor = executor;
            this.automaticShutdown = automaticShutdown;
        }
    }
}
