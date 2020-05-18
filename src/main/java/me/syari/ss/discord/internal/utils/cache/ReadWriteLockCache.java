package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.internal.utils.UnlockHook;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ReadWriteLockCache<T> {
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected WeakReference<List<T>> cachedList;

    public UnlockHook writeLock() {
        if (lock.getReadHoldCount() > 0) throw new IllegalStateException("Unable to acquire write-lock while holding read-lock!");
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        clearCachedLists();
        return new UnlockHook(writeLock);
    }

    public UnlockHook readLock() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        return new UnlockHook(readLock);
    }

    public void clearCachedLists() {
        cachedList = null;
    }

    protected List<T> getCachedList() {
        return cachedList == null ? null : cachedList.get();
    }

    protected List<T> cache(List<T> list) {
        list = Collections.unmodifiableList(list);
        cachedList = new WeakReference<>(list);
        return list;
    }

}
