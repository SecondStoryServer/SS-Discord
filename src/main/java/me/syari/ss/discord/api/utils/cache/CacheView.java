package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.cache.UnifiedCacheViewImpl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;


public interface CacheView<T> extends Iterable<T> {

    @Nonnull
    List<T> asList();


    @Nonnull
    ClosableIterator<T> lockedIterator();


    long size();


    boolean isEmpty();


    @Nonnull
    List<T> getElementsByName(@Nonnull String name, boolean ignoreCase);


    @Nonnull
    Stream<T> stream();


    @Nonnull
    Stream<T> parallelStream();


    @Nonnull
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@Nonnull Supplier<? extends Stream<? extends SnowflakeCacheView<E>>> generator) {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(generator);
    }


}
