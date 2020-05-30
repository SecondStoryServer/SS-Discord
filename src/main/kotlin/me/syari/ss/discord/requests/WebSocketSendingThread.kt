package me.syari.ss.discord.requests

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal class WebSocketSendingThread: Runnable {
    private val chunkSyncQueue = WebSocketClient.chunkSyncQueue
    private val ratelimitQueue = WebSocketClient.ratelimitQueue
    private val executor = WebSocketClient.executor
    private var handle: Future<*>? = null
    private var needRateLimit = false
    private var attemptedToSend = false
    private var shutdown = false

    fun shutdown() {
        shutdown = true
        handle?.cancel(false)
    }

    fun start() {
        shutdown = false
        handle = executor.submit(this)
    }

    override fun run() {
        if (!WebSocketClient.sentAuthInfo) {
            if (shutdown) return
            handle = executor.schedule(this, 500, TimeUnit.MILLISECONDS)
            return
        }
        try {
            attemptedToSend = false
            needRateLimit = false
            WebSocketClient.queueLock.lockInterruptibly()
            val chunkOrSyncRequest = chunkSyncQueue.peek()
            if (chunkOrSyncRequest != null) {
                if (send(chunkOrSyncRequest)) chunkSyncQueue.remove()
            } else {
                val message = ratelimitQueue.peek()
                if (message != null && send(message)) ratelimitQueue.remove()
            }
            if (shutdown) return
            handle = when {
                needRateLimit -> executor.schedule(this, 1, TimeUnit.MINUTES)
                attemptedToSend -> executor.schedule(this, 10, TimeUnit.MILLISECONDS)
                else -> executor.schedule(this, 500, TimeUnit.MILLISECONDS)
            }
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        } finally {
            WebSocketClient.maybeUnlock()
        }
    }

    private fun send(request: String): Boolean {
        needRateLimit = !WebSocketClient.send(request, false)
        attemptedToSend = true
        return !needRateLimit
    }
}