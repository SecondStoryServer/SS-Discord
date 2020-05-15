

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.ISnowflake;

import javax.annotation.Nonnull;
import java.util.NavigableSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Specialized {@link CacheView} for entities that occur in a specified order.
 * <br>In this specialization {@link #forEach(Consumer)} will copy the underlying data store
 * in order to preserve order on iterations, use {@link #forEachUnordered(Consumer)} to avoid this overhead.
 *
 * @param <T>
 *        The entity type
 *
 * @see   CacheView CacheView for details on Efficient Memory Usage
 */
public interface SortedSnowflakeCacheView<T extends Comparable<? super T> & ISnowflake> extends SnowflakeCacheView<T>
{
    /**
     * Behavior similar to {@link CacheView#forEach(Consumer)} but does not preserve order.
     * <br>This will not copy the data store as sorting is not needed.
     *
     * @param  action
     *         The action to perform
     *
     * @throws NullPointerException
     *         If provided with null
     *
     * @since  4.0.0
     */
    void forEachUnordered(@Nonnull final Consumer<? super T> action);

    @Nonnull
    @Override
    NavigableSet<T> asSet();

    /**
     * Behavior similar to {@link CacheView#stream()} which does not preserve order.
     *
     * @return Stream of the contained elements
     *
     * @since  4.0.0
     */
    @Nonnull
    Stream<T> streamUnordered();

    /**
     * Behavior similar to {@link CacheView#parallelStream()} which does not preserve order.
     *
     * @return (Parallel) Stream of contained elements
     *
     * @since  4.0.0
     */
    @Nonnull
    Stream<T> parallelStreamUnordered();
}
