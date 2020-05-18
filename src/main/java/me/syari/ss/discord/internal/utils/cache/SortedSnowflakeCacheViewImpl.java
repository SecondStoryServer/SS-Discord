package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.internal.utils.UnlockHook;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SortedSnowflakeCacheViewImpl<T extends ISnowflake & Comparable<? super T>> extends SnowflakeCacheViewImpl<T> {
    protected static final int SPLIT_CHARACTERISTICS = Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL;

    protected final Comparator<T> comparator;

    public SortedSnowflakeCacheViewImpl(Class<T> type, Comparator<T> comparator) {
        super(type);
        this.comparator = comparator;
    }

    @Override
    public void forEach(@NotNull Consumer<? super T> action) {
        try (UnlockHook hook = readLock()) {
            iterator().forEachRemaining(action);
        }
    }

    @NotNull
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
            list.sort(comparator);
            return cache(list);
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        try (UnlockHook hook = readLock()) {
            return Spliterators.spliterator(iterator(), elements.size(), SPLIT_CHARACTERISTICS);
        }
    }

    @NotNull
    @Override
    public Stream<T> stream() {
        return super.stream().sorted(comparator);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        try (UnlockHook hook = readLock()) {
            T[] arr = elements.values(emptyArray);
            Arrays.sort(arr, comparator);
            return new ObjectArrayIterator<>(arr);
        }
    }
}
