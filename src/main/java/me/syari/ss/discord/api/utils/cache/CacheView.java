package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.cache.UnifiedCacheViewImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public interface CacheView<T> extends Iterable<T> {

    @Nonnull
    List<T> asList();


    @Nonnull
    ClosableIterator<T> lockedIterator();


    @Nullable
    default <R> R applyStream(@Nonnull Function<? super Stream<T>, ? extends R> action) {
        Checks.notNull(action, "Action");
        try (ClosableIterator<T> it = lockedIterator()) {
            Spliterator<T> spliterator = Spliterators.spliterator(it, size(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
            Stream<T> stream = StreamSupport.stream(spliterator, false);
            return action.apply(stream);
        }
    }


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
