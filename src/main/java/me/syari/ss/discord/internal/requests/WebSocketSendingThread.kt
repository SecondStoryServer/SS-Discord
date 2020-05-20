package me.syari.ss.discord.internal.requests

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class WebSocketSendingThread(private val client: WebSocketClient): Runnable {
    private val chunkSyncQueue = client.chunkSyncQueue
    private val ratelimitQueue = client.ratelimitQueue
    private val executor = client.executor
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
        if (!client.sentAuthInfo) {
            if (shutdown) return
            handle = executor.schedule(this, 500, TimeUnit.MILLISECONDS)
            return
        }
        try {
            attemptedToSend = false
            needRateLimit = false
            client.queueLock.lockInterruptibly()
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
            client.maybeUnlock()
        }
    }

    private fun send(request: String): Boolean {
        needRateLimit = !client.send(request, false)
        attemptedToSend = true
        return !needRateLimit
    }
}