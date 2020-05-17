package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.internal.utils.Checks;
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

    long size();

    boolean isEmpty();

    @NotNull
    Stream<T> stream();

    @NotNull
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@NotNull Supplier<? extends Stream<? extends SnowflakeCacheView<E>>> generator) {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(generator);
    }
}
