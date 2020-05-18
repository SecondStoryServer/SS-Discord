package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.internal.utils.cache.UnifiedCacheViewImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface CacheView<T> extends Iterable<T> {
    @NotNull
    List<T> asList();

    @NotNull
    ClosableIterator<T> lockedIterator();

    boolean isEmpty();

    @NotNull
    Stream<T> stream();

    @NotNull
    static <E extends ISnowflake> ISnowflakeCacheView<E> allSnowflakes(@NotNull Supplier<? extends Stream<? extends ISnowflakeCacheView<E>>> generator) {
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(generator);
    }
}
