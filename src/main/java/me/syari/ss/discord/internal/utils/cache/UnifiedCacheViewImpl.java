package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.utils.ChainedClosableIterator;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnifiedCacheViewImpl<T, E extends CacheView<T>> implements CacheView<T> {
    protected final Supplier<? extends Stream<? extends E>> generator;

    public UnifiedCacheViewImpl(Supplier<? extends Stream<? extends E>> generator) {
        this.generator = generator;
    }

    @Override
    public long size() {
        return distinctStream().mapToLong(CacheView::size).sum();
    }

    @Override
    public boolean isEmpty() {
        return distinctStream().allMatch(CacheView::isEmpty);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        try (ClosableIterator<T> it = lockedIterator()) {
            while (it.hasNext())
                action.accept(it.next());
        }
    }

    @Nonnull
    @Override
    public List<T> asList() {
        List<T> list = new LinkedList<>();
        forEach(list::add);
        return Collections.unmodifiableList(list);
    }

    @Nonnull
    @Override
    public ChainedClosableIterator<T> lockedIterator() {
        Iterator<? extends E> gen = generator.get().iterator();
        return new ChainedClosableIterator<>(gen);
    }

    @Nonnull
    @Override
    public List<T> getElementsByName(@Nonnull String name, boolean ignoreCase) {
        return Collections.unmodifiableList(distinctStream()
                .flatMap(view -> view.getElementsByName(name, ignoreCase).stream())
                .distinct()
                .collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public Stream<T> stream() {
        return distinctStream().flatMap(CacheView::stream).distinct();
    }

    @Nonnull
    @Override
    public Stream<T> parallelStream() {
        return distinctStream().flatMap(CacheView::parallelStream).distinct();
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    protected Stream<? extends E> distinctStream() {
        return generator.get().distinct();
    }

    public static class UnifiedSnowflakeCacheView<T extends ISnowflake>
            extends UnifiedCacheViewImpl<T, SnowflakeCacheView<T>> implements SnowflakeCacheView<T> {
        public UnifiedSnowflakeCacheView(Supplier<? extends Stream<? extends SnowflakeCacheView<T>>> generator) {
            super(generator);
        }

        @Override
        public T getElementById(long id) {
            return generator.get()
                    .map(view -> view.getElementById(id))
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);
        }
    }

}
