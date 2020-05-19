package me.syari.ss.discord.api.utils

import java.util.NoSuchElementException
import java.util.concurrent.locks.Lock

class LockIterator<T>(
    private val iterator: Iterator<T>, private var lock: Lock?
): ClosableIterator<T> {
    override fun close() {
        lock?.let {
            it.unlock()
            lock = null
        }
    }

    override fun hasNext(): Boolean {
        if (lock == null) {
            return false
        }
        val hasNext = iterator.hasNext()
        if (!hasNext) {
            close()
        }
        return hasNext
    }

    override fun next(): T {
        return if (lock != null) {
            iterator.next()
        } else {
            throw NoSuchElementException()
        }
    }

    override fun remove() {
        throw UnsupportedOperationException("remove")
    }
}