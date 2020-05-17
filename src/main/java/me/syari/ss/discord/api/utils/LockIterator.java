package me.syari.ss.discord.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

public class LockIterator<T> implements ClosableIterator<T> {
    private final Iterator<? extends T> it;
    private Lock lock;

    public LockIterator(@NotNull Iterator<? extends T> it, Lock lock) {
        this.it = it;
        this.lock = lock;
    }

    @Override
    public void close() {
        if (lock != null)
            lock.unlock();
        lock = null;
    }

    @Override
    public boolean hasNext() {
        if (lock == null)
            return false;
        boolean hasNext = it.hasNext();
        if (!hasNext)
            close();
        return hasNext;
    }

    @NotNull
    @Override
    public T next() {
        if (lock == null)
            throw new NoSuchElementException();
        return it.next();
    }
}
