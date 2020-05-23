package me.syari.ss.discord.requests

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

class CountingThreadFactory(private val specifier: String): ThreadFactory {
    private val count = AtomicLong(1)

    override fun newThread(r: Runnable): Thread {
        return Thread(r, "$specifier-Worker ${count.getAndIncrement()}").apply {
            isDaemon = true
        }
    }
}