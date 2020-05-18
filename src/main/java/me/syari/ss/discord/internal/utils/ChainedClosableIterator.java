package me.syari.ss.discord.internal.utils;

import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.api.utils.cache.CacheView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ChainedClosableIterator<T> implements ClosableIterator<T> {
    private final Set<T> items = new HashSet<>();
    private final Iterator<? extends CacheView<T>> generator;
    private ClosableIterator<T> currentIterator;
    private T item;

    public ChainedClosableIterator(Iterator<? extends CacheView<T>> generator) {
        this.generator = generator;
    }

    @Override
    public void close() {
        if (currentIterator != null) {
            currentIterator.close();
            currentIterator = null;
        }
    }

    @Override
    public boolean hasNext() {
        if (item != null) return true;
        if (currentIterator != null) {
            if (currentIterator.hasNext() && findNext()) return true;
            currentIterator.close();
            currentIterator = null;
        }
        return processChain();
    }

    private boolean processChain() {
        while (item == null) {
            CacheView<T> view = null;
            while (generator.hasNext()) {
                view = generator.next();
                if (view.isEmpty()) {
                    view = null;
                } else {
                    break;
                }
            }
            if (view == null) return false;
            currentIterator = view.lockedIterator();
            if (findNext()) break;
        }
        return true;
    }

    private boolean findNext() {
        while (currentIterator.hasNext()) {
            T next = currentIterator.next();
            if (!items.contains(next)) {
                item = next;
                items.add(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (hasNext()) {
            T tmp = item;
            item = null;
            return tmp;
        } else {
            throw new NoSuchElementException();
        }
    }
}
