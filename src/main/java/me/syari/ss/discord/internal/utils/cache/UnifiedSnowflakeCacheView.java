package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;
import me.syari.ss.discord.internal.utils.ChainedClosableIterator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UnifiedSnowflakeCacheView<T extends ISnowflake> implements ISnowflakeCacheView<T> {
    protected final Supplier<? extends Stream<? extends ISnowflakeCacheView<T>>> generator;

    public UnifiedSnowflakeCacheView(Supplier<? extends Stream<? extends ISnowflakeCacheView<T>>> generator) {
        this.generator = generator;
    }

    @Override
    public T getElementById(long id) {
        return generator.get()
                .map(view -> view.getElementById(id))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    @Override
    public boolean isEmpty() {
        return distinctStream().allMatch(CacheView::isEmpty);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        try (ClosableIterator<T> iterator = lockedIterator()) {
            while (iterator.hasNext()) {
                action.accept(iterator.next());
            }
        }
    }

    @NotNull
    @Override
    public List<T> asList() {
        List<T> list = new LinkedList<>();
        forEach(list::add);
        return Collections.unmodifiableList(list);
    }

    @NotNull
    @Override
    public ChainedClosableIterator<T> lockedIterator() {
        Iterator<? extends ISnowflakeCacheView<T>> iterator = generator.get().iterator();
        return new ChainedClosableIterator<>(iterator);
    }

    @NotNull
    @Override
    public Stream<T> stream() {
        return distinctStream().flatMap(CacheView::stream).distinct();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    protected Stream<? extends ISnowflakeCacheView<T>> distinctStream() {
        return generator.get().distinct();
    }
}
