package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public interface PaginationAction<T, M extends PaginationAction<T, M>> extends RestAction<List<T>>, Iterable<T> {


    @Nonnull
    @Override
    M setCheck(@Nullable BooleanSupplier checks);


    boolean isEmpty();


    @Nonnull
    M cache(final boolean enableCache);


    int getMaxLimit();


    int getLimit();


    @Override
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.IMMUTABLE);
    }


    @Nonnull
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }


    @Nonnull
    @Override
    PaginationIterator<T> iterator();


    class PaginationIterator<E> implements Iterator<E> {
        protected Queue<E> items;
        protected final Supplier<List<E>> supply;

        public PaginationIterator(Collection<E> queue, Supplier<List<E>> supply) {
            this.items = new LinkedList<>(queue);
            this.supply = supply;
        }

        @Override
        public boolean hasNext() {
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
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException("Reached End of pagination task!");
            return items.poll();
        }

        protected boolean hitEnd() {
            return items.isEmpty();
        }
    }
}
