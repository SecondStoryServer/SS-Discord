package me.syari.ss.discord.api

import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object SessionController {
    const val IDENTIFY_DELAY = 5

    private val lock = Any()
    private val connectQueue: Queue<SessionConnectNode> = ConcurrentLinkedQueue()
    private val globalRatelimit = AtomicLong(Long.MIN_VALUE)
    private var workerHandle: Thread? = null
    private var lastConnect = 0L

    fun appendSession(node: SessionConnectNode) {
        removeSession(node)
        connectQueue.add(node)
        runWorker()
    }

    fun removeSession(node: SessionConnectNode) {
        connectQueue.remove(node)
    }

    fun getGlobalRatelimit(): Long {
        return globalRatelimit.get()
    }

    fun setGlobalRatelimit(ratelimit: Long) {
        globalRatelimit.set(ratelimit)
    }

    fun getGateway(): String {
        val route = Route.gatewayRoute
        return RestAction(route) { response: Response, _: Request<String> ->
            response.dataObject.getString("url")
        }.complete()
    }

    private fun runWorker() {
        synchronized(lock) {
            if (workerHandle == null) {
                workerHandle = QueueWorker().apply {
                    start()
                }
            }
        }
    }

    private class QueueWorker: Thread("SessionControllerAdapter-Worker") {
        private val delay = TimeUnit.SECONDS.toMillis(IDENTIFY_DELAY.toLong())
        override fun run() {
            try {
                if (0 < delay) {
                    val interval = System.currentTimeMillis() - lastConnect
                    if (interval < delay) {
                        sleep(delay - interval)
                    }
                }
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
            processQueue()
            synchronized(lock) {
                workerHandle = null
                if (!connectQueue.isEmpty()) {
                    runWorker()
                }
            }
        }

        private fun processQueue() {
            var isMultiple = connectQueue.size > 1
            while (!connectQueue.isEmpty()) {
                val node = connectQueue.poll()
                try {
                    node.run(isMultiple && connectQueue.isEmpty())
                    isMultiple = true
                    lastConnect = System.currentTimeMillis()
                    if (connectQueue.isEmpty()) break
                    if (delay > 0) {
                        sleep(delay)
                    }
                } catch (ex: IllegalStateException) {
                    appendSession(node)
                } catch (ex: InterruptedException) {
                    appendSession(node)
                }
            }
        }
    }

    interface SessionConnectNode {
        @Throws(InterruptedException::class)
        fun run(isLast: Boolean)
    }
}