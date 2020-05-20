package me.syari.ss.discord.internal.utils

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier

class CountingThreadFactory(identifier: Supplier<String>, specifier: String): ThreadFactory {
    private val identifier: Supplier<String>
    private val count = AtomicLong(1)
    override fun newThread(r: Runnable): Thread {
        val thread = Thread(r, identifier.get() + "-Worker " + count.getAndIncrement())
        thread.isDaemon = true
        return thread
    }

    init {
        this.identifier = Supplier { identifier.get() + " " + specifier }
    }
}