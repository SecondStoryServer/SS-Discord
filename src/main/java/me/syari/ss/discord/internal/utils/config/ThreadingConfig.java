package me.syari.ss.discord.internal.utils.config;

import me.syari.ss.discord.internal.utils.concurrent.CountingThreadFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ThreadingConfig {
    private ScheduledExecutorService rateLimitPool;
    private ScheduledExecutorService gatewayPool;
    private ExecutorService callbackPool;

    private boolean shutdownRateLimitPool;
    private boolean shutdownGatewayPool;
    private boolean shutdownCallbackPool;

    public ThreadingConfig() {
        this.callbackPool = ForkJoinPool.commonPool();

        this.shutdownRateLimitPool = true;
        this.shutdownGatewayPool = true;
        this.shutdownCallbackPool = false;
    }

    public void setRateLimitPool(@Nullable ScheduledExecutorService executor, boolean shutdown) {
        this.rateLimitPool = executor;
        this.shutdownRateLimitPool = shutdown;
    }

    public void setGatewayPool(@Nullable ScheduledExecutorService executor, boolean shutdown) {
        this.gatewayPool = executor;
        this.shutdownGatewayPool = shutdown;
    }

    public void setCallbackPool(@Nullable ExecutorService executor, boolean shutdown) {
        this.callbackPool = executor == null ? ForkJoinPool.commonPool() : executor;
        this.shutdownCallbackPool = shutdown;
    }

    public void init(@NotNull Supplier<String> identifier) {
        if (this.rateLimitPool == null)
            this.rateLimitPool = newScheduler(5, identifier, "RateLimit");
        if (this.gatewayPool == null)
            this.gatewayPool = newScheduler(1, identifier, "Gateway");
    }

    public void shutdown() {
        if (shutdownCallbackPool)
            callbackPool.shutdown();
        if (shutdownGatewayPool)
            gatewayPool.shutdown();
        if (shutdownRateLimitPool) {
            if (rateLimitPool instanceof ScheduledThreadPoolExecutor) {
                ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) rateLimitPool;
                executor.setKeepAliveTime(5L, TimeUnit.SECONDS);
                executor.allowCoreThreadTimeOut(true);
            } else {
                rateLimitPool.shutdown();
            }
        }
    }

    public void shutdownNow() {
        if (shutdownCallbackPool)
            callbackPool.shutdownNow();
        if (shutdownGatewayPool)
            gatewayPool.shutdownNow();
        if (shutdownRateLimitPool)
            rateLimitPool.shutdownNow();
    }

    @NotNull
    public ScheduledExecutorService getRateLimitPool() {
        return rateLimitPool;
    }

    @NotNull
    public ScheduledExecutorService getGatewayPool() {
        return gatewayPool;
    }

    @NotNull
    public ExecutorService getCallbackPool() {
        return callbackPool;
    }

    @NotNull
    public static ScheduledThreadPoolExecutor newScheduler(int coreSize, Supplier<String> identifier, String baseName) {
        return new ScheduledThreadPoolExecutor(coreSize, new CountingThreadFactory(identifier, baseName));
    }

    @NotNull
    public static ThreadingConfig getDefault() {
        return new ThreadingConfig();
    }
}
