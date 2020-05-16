package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.internal.utils.UnlockHook;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ReadWriteLockCache<T> {
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected WeakReference<List<T>> cachedList;
    protected WeakReference<Set<T>> cachedSet;

    public UnlockHook writeLock() {
        if (lock.getReadHoldCount() > 0)
            throw new IllegalStateException("Unable to acquire write-lock while holding read-lock!");
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        onAcquireWriteLock();
        clearCachedLists();
        return new UnlockHook(writeLock);
    }

    public UnlockHook readLock() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        onAcquireReadLock();
        return new UnlockHook(readLock);
    }

    public void clearCachedLists() {
        cachedList = null;
        cachedSet = null;
    }

    protected void onAcquireWriteLock() {
    }

    protected void onAcquireReadLock() {
    }

    protected List<T> getCachedList() {
        return cachedList == null ? null : cachedList.get();
    }

    protected Set<T> getCachedSet() {
        return cachedSet == null ? null : cachedSet.get();
    }

    protected List<T> cache(List<T> list) {
        list = Collections.unmodifiableList(list);
        cachedList = new WeakReference<>(list);
        return list;
    }

    protected Set<T> cache(Set<T> set) {
        set = Collections.unmodifiableSet(set);
        cachedSet = new WeakReference<>(set);
        return set;
    }

    protected NavigableSet<T> cache(NavigableSet<T> set) {
        set = Collections.unmodifiableNavigableSet(set);
        cachedSet = new WeakReference<>(set);
        return set;
    }
}
