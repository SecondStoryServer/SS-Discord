package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.internal.utils.UnlockHook;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SortedSnowflakeCacheViewImpl<T extends ISnowflake & Comparable<? super T>>
        extends SnowflakeCacheViewImpl<T> implements SortedSnowflakeCacheView<T> {
    protected static final int SPLIT_CHARACTERISTICS = Spliterator.IMMUTABLE | Spliterator.ORDERED | Spliterator.NONNULL;

    protected final Comparator<T> comparator;

    public SortedSnowflakeCacheViewImpl(Class<T> type, Function<T, String> nameMapper, Comparator<T> comparator) {
        super(type, nameMapper);
        this.comparator = comparator;
    }

    @Override
    public void forEach(@Nonnull Consumer<? super T> action) {
        try (UnlockHook hook = readLock()) {
            iterator().forEachRemaining(action);
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

    @Nonnull
    @Override
    public Stream<T> stream() {
        return super.stream().sorted(comparator);
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        try (UnlockHook hook = readLock()) {
            T[] arr = elements.values(emptyArray);
            Arrays.sort(arr, comparator);
            return new ObjectArrayIterator<>(arr);
        }
    }
}
