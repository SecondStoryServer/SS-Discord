package me.syari.ss.discord.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

public class LockIterator<T> implements ClosableIterator<T> {
    private final Iterator<? extends T> iterator;
    private Lock lock;

    public LockIterator(@NotNull Iterator<? extends T> iterator, Lock lock) {
        this.iterator = iterator;
        this.lock = lock;
    }

    @Override
    public void close() {
        if (lock != null) {
            lock.unlock();
            lock = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (lock == null) {
            return false;
        }
        boolean hasNext = iterator.hasNext();
        if (!hasNext) {
            close();
        }
        return hasNext;
    }

    @NotNull
    @Override
    public T next() {
        if (lock != null) {
            return iterator.next();
        } else {
            throw new NoSuchElementException();
        }
    }
}
