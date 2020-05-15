

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.utils.ClosableIterator;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.cache.AbstractCacheView;
import me.syari.ss.discord.internal.utils.cache.ShardCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.SortedSnowflakeCacheViewImpl;
import me.syari.ss.discord.internal.utils.cache.UnifiedCacheViewImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public interface CacheView<T> extends Iterable<T>
{

    @Nonnull
    List<T> asList();


    @Nonnull
    Set<T> asSet();


    @Nonnull
    ClosableIterator<T> lockedIterator();


    default void forEachUnordered(@Nonnull final Consumer<? super T> action)
    {
        forEach(action);
    }


    @Nullable
    default <R> R applyStream(@Nonnull Function<? super Stream<T>, ? extends R> action)
    {
        Checks.notNull(action, "Action");
        try (ClosableIterator<T> it = lockedIterator())
        {
            Spliterator<T> spliterator = Spliterators.spliterator(it, size(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
            Stream<T> stream = StreamSupport.stream(spliterator, false);
            return action.apply(stream);
        }
    }


    default void acceptStream(@Nonnull Consumer<? super Stream<T>> action)
    {
        Checks.notNull(action, "Action");
        try (ClosableIterator<T> it = lockedIterator())
        {
            Spliterator<T> spliterator = Spliterators.spliterator(it, size(), Spliterator.IMMUTABLE | Spliterator.NONNULL);
            Stream<T> stream = StreamSupport.stream(spliterator, false);
            action.accept(stream);
        }
    }


    long size();


    boolean isEmpty();


    @Nonnull
    List<T> getElementsByName(@Nonnull String name, boolean ignoreCase);


    @Nonnull
    default List<T> getElementsByName(@Nonnull String name)
    {
        return getElementsByName(name, false);
    }


    @Nonnull
    Stream<T> stream();


    @Nonnull
    Stream<T> parallelStream();


    @Nonnull
    default <R, A> R collect(@Nonnull Collector<? super T, A, R> collector)
    {
        return stream().collect(collector);
    }


    @Nonnull
    static <E> CacheView<E> all(@Nonnull Collection<? extends CacheView<E>> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl<>(cacheViews::stream);
    }


    @Nonnull
    static <E> CacheView<E> all(@Nonnull Supplier<? extends Stream<? extends CacheView<E>>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl<>(generator);
    }


    @Nonnull
    static ShardCacheView allShards(@Nonnull Collection<ShardCacheView> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(cacheViews::stream);
    }


    @Nonnull
    static ShardCacheView allShards(@Nonnull Supplier<? extends Stream<? extends ShardCacheView>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new ShardCacheViewImpl.UnifiedShardCacheViewImpl(generator);
    }


    @Nonnull
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@Nonnull Collection<? extends SnowflakeCacheView<E>> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(cacheViews::stream);
    }


    @Nonnull
    static <E extends ISnowflake> SnowflakeCacheView<E> allSnowflakes(@Nonnull Supplier<? extends Stream<? extends SnowflakeCacheView<E>>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedSnowflakeCacheView<>(generator);
    }


    @Nonnull
    static UnifiedMemberCacheView allMembers(@Nonnull Collection<? extends MemberCacheView> cacheViews)
    {
        Checks.noneNull(cacheViews, "Collection");
        return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(cacheViews::stream);
    }


    @Nonnull
    static UnifiedMemberCacheView allMembers(@Nonnull Supplier<? extends Stream<? extends MemberCacheView>> generator)
    {
        Checks.notNull(generator, "Generator");
        return new UnifiedCacheViewImpl.UnifiedMemberCacheViewImpl(generator);
    }


    class SimpleCacheView<T> extends AbstractCacheView<T>
    {
        public SimpleCacheView(@Nonnull Class<T> type, @Nullable Function<T, String> nameMapper)
        {
            super(type, nameMapper);
        }
    }
}
