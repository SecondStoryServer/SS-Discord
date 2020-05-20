package me.syari.ss.discord.internal.utils

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier

class CountingThreadFactory(identifier: () -> String, specifier: String): ThreadFactory {
    private val identifier = Supplier { identifier.invoke() + " " + specifier }
    private val count = AtomicLong(1)

    override fun newThread(r: Runnable): Thread {
        return Thread(r, identifier.get() + "-Worker " + count.getAndIncrement()).apply {
            isDaemon = true
        }
    }
}