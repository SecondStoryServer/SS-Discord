package me.syari.ss.discord.internal.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadingConfig {
    lateinit var rateLimitPool: ScheduledExecutorService
        private set
    lateinit var gatewayPool: ScheduledExecutorService
        private set
    val callbackPool: ExecutorService = ForkJoinPool.commonPool()

    fun init(identifier: () -> String) {
        rateLimitPool = newScheduler(
            5, identifier, "RateLimit"
        )
        gatewayPool = newScheduler(
            1, identifier, "Gateway"
        )
    }

    fun shutdown() {
        callbackPool.shutdown()
        gatewayPool.shutdown()
        if (rateLimitPool is ScheduledThreadPoolExecutor) {
            val executor = rateLimitPool as ScheduledThreadPoolExecutor
            executor.setKeepAliveTime(5L, TimeUnit.SECONDS)
            executor.allowCoreThreadTimeOut(true)
        } else {
            rateLimitPool.shutdown()
        }
    }

    companion object {
        fun newScheduler(
            coreSize: Int, identifier: () -> String, baseName: String
        ): ScheduledThreadPoolExecutor {
            return ScheduledThreadPoolExecutor(coreSize, CountingThreadFactory(identifier, baseName))
        }
    }
}