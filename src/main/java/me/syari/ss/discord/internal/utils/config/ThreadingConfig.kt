package me.syari.ss.discord.internal.utils.config

import me.syari.ss.discord.internal.utils.CountingThreadFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class ThreadingConfig {
    private var rateLimitPool: ScheduledExecutorService? = null
    private var gatewayPool: ScheduledExecutorService? = null
    val callbackPool: ExecutorService = ForkJoinPool.commonPool()
    fun init(identifier: Supplier<String>) {
        rateLimitPool = newScheduler(5, identifier, "RateLimit")
        gatewayPool = newScheduler(1, identifier, "Gateway")
    }

    fun shutdown() {
        callbackPool.shutdown()
        gatewayPool!!.shutdown()
        if (rateLimitPool is ScheduledThreadPoolExecutor) {
            val executor = rateLimitPool as ScheduledThreadPoolExecutor
            executor.setKeepAliveTime(5L, TimeUnit.SECONDS)
            executor.allowCoreThreadTimeOut(true)
        } else {
            rateLimitPool!!.shutdown()
        }
    }

    fun shutdownNow() {
        callbackPool.shutdownNow()
        gatewayPool!!.shutdownNow()
        rateLimitPool!!.shutdownNow()
    }

    fun getRateLimitPool(): ScheduledExecutorService {
        return rateLimitPool!!
    }

    fun getGatewayPool(): ScheduledExecutorService {
        return gatewayPool!!
    }

    companion object {
        fun newScheduler(
            coreSize: Int, identifier: Supplier<String>, baseName: String
        ): ScheduledThreadPoolExecutor {
            return ScheduledThreadPoolExecutor(coreSize, CountingThreadFactory(identifier, baseName))
        }
    }
}