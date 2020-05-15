

package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.Procedure;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public interface PaginationAction<T, M extends PaginationAction<T, M>> extends RestAction<List<T>>, Iterable<T>
{

    @Nonnull
    M skipTo(long id);


    long getLastKey();

    @Nonnull
    @Override
    M setCheck(@Nullable BooleanSupplier checks);


    int cacheSize();


    boolean isEmpty();


    @Nonnull
    List<T> getCached();


    @Nonnull
    T getLast();


    @Nonnull
    T getFirst();


    @Nonnull
    M limit(final int limit);


    @Nonnull
    M cache(final boolean enableCache);


    boolean isCacheEnabled();


    int getMaxLimit();


    int getMinLimit();


    int getLimit();


    @Nonnull
    default CompletableFuture<List<T>> takeWhileAsync(@Nonnull final Predicate<? super T> rule)
    {
        Checks.notNull(rule, "Rule");
        return takeUntilAsync(rule.negate());
    }


    @Nonnull
    default CompletableFuture<List<T>> takeWhileAsync(int limit, @Nonnull final Predicate<? super T> rule)
    {
        Checks.notNull(rule, "Rule");
        return takeUntilAsync(limit, rule.negate());
    }


    @Nonnull
    default CompletableFuture<List<T>> takeUntilAsync(@Nonnull final Predicate<? super T> rule)
    {
        return takeUntilAsync(0, rule);
    }


    @Nonnull
    default CompletableFuture<List<T>> takeUntilAsync(int limit, @Nonnull final Predicate<? super T> rule)
    {
        Checks.notNull(rule, "Rule");
        Checks.notNegative(limit, "Limit");
        List<T> result = new ArrayList<>();
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        CompletableFuture<?> handle = forEachAsync((element) -> {
            if (rule.test(element))
                return false;
            result.add(element);
            return limit == 0 || limit > result.size();
        });
        handle.whenComplete((r, t) -> {
           if (t != null)
               future.completeExceptionally(t);
           else
               future.complete(result);
        });
        return future;
    }


    @Nonnull
    CompletableFuture<List<T>> takeAsync(int amount);


    @Nonnull
    CompletableFuture<List<T>> takeRemainingAsync(int amount);


    @Nonnull
    default CompletableFuture<?> forEachAsync(@Nonnull final Procedure<? super T> action)
    {
        return forEachAsync(action, RestActionImpl.getDefaultFailure());
    }


    @Nonnull
    CompletableFuture<?> forEachAsync(@Nonnull final Procedure<? super T> action, @Nonnull final Consumer<? super Throwable> failure);


    @Nonnull
    default CompletableFuture<?> forEachRemainingAsync(@Nonnull final Procedure<? super T> action)
    {
        return forEachRemainingAsync(action, RestActionImpl.getDefaultFailure());
    }


    @Nonnull
    CompletableFuture<?> forEachRemainingAsync(@Nonnull final Procedure<? super T> action, @Nonnull final Consumer<? super Throwable> failure);


    void forEachRemaining(@Nonnull final Procedure<? super T> action);

    @Override
    default Spliterator<T> spliterator()
    {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE);
    }


    @Nonnull
    default Stream<T> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }


    @Nonnull
    default Stream<T> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }


    @Nonnull
    @Override
    PaginationIterator<T> iterator();


    class PaginationIterator<E> implements Iterator<E>
    {
        protected Queue<E> items;
        protected final Supplier<List<E>> supply;

        public PaginationIterator(Collection<E> queue, Supplier<List<E>> supply)
        {
            this.items = new LinkedList<>(queue);
            this.supply = supply;
        }

        @Override
        public boolean hasNext()
        {
            if (items == null)
                return false;
            if (!hitEnd())
                return true;

            if (items.addAll(supply.get()))
                return true;

            // null indicates that the real end has been reached
            items = null;
            return false;
        }

        @Override
        public E next()
        {
            if (!hasNext())
                throw new NoSuchElementException("Reached End of pagination task!");
            return items.poll();
        }

        protected boolean hitEnd()
        {
            return items.isEmpty();
        }
    }
}
