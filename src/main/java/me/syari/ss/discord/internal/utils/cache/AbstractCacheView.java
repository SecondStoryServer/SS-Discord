package me.syari.ss.discord.internal.utils.cache;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.utils.LockIterator;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.internal.utils.UnlockHook;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractCacheView<T> extends ReadWriteLockCache<T> implements CacheView<T> {
    protected final TLongObjectMap<T> elements = new TLongObjectHashMap<>();
    protected final T[] emptyArray;

    @SuppressWarnings("unchecked")
    protected AbstractCacheView(Class<T> type) {
        this.emptyArray = (T[]) Array.newInstance(type, 0);
    }

    public void clear() {
        try (UnlockHook hook = writeLock()) {
            elements.clear();
        }
    }

    public TLongObjectMap<T> getMap() {
        if (!lock.writeLock().isHeldByCurrentThread())
            throw new IllegalStateException("Cannot access map directly without holding write lock!");
        return elements;
    }

    public T get(long id) {
        try (UnlockHook hook = readLock()) {
            return elements.get(id);
        }
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        try (UnlockHook hook = readLock()) {
            for (T elem : elements.valueCollection()) {
                action.accept(elem);
            }
        }
    }

    @Nonnull
    @Override
    public LockIterator<T> lockedIterator() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            Iterator<T> directIterator = elements.valueCollection().iterator();
            return new LockIterator<>(directIterator, readLock);
        } catch (Throwable t) {
            readLock.unlock();
            throw t;
        }
    }

    @Nonnull
    @Override
    public List<T> asList() {
        if (isEmpty())
            return Collections.emptyList();
        try (UnlockHook hook = readLock()) {
            List<T> list = getCachedList();
            if (list != null)
                return list;
            list = new ArrayList<>(elements.size());
            elements.forEachValue(list::add);
            return cache(list);
        }
    }

    @Override
    public long size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public Spliterator<T> spliterator() {
        try (UnlockHook hook = readLock()) {
            return Spliterators.spliterator(elements.values(), Spliterator.IMMUTABLE);
        }
    }

    @Nonnull
    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        try (UnlockHook hook = readLock()) {
            return new ObjectArrayIterator<>(elements.values(emptyArray));
        }
    }

    @Override
    public String toString() {
        return asList().toString();
    }

    @Override
    public int hashCode() {
        try (UnlockHook hook = readLock()) {
            return elements.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof AbstractCacheView))
            return false;
        AbstractCacheView view = (AbstractCacheView) obj;
        try (UnlockHook hook = readLock(); UnlockHook otherHook = view.readLock()) {
            return this.elements.equals(view.elements);
        }
    }

}
