package me.syari.ss.discord.internal.requests.restaction.pagination;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.restaction.pagination.PaginationAction;
import me.syari.ss.discord.api.utils.Procedure;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class PaginationActionImpl<T, M extends PaginationAction<T, M>>
        extends RestActionImpl<List<T>>
        implements PaginationAction<T, M> {
    protected final List<T> cached = new CopyOnWriteArrayList<>();
    protected final int maxLimit;
    protected final int minLimit;
    protected final AtomicInteger limit;

    protected volatile long iteratorIndex = 0;
    protected volatile long lastKey = 0;
    protected volatile T last = null;
    protected volatile boolean useCache = true;


    public PaginationActionImpl(JDA api, Route.CompiledRoute route, int minLimit, int maxLimit, int initialLimit) {
        super(api, route);
        this.maxLimit = maxLimit;
        this.minLimit = minLimit;
        this.limit = new AtomicInteger(initialLimit);
    }


    public PaginationActionImpl(JDA api) {
        super(api, null);
        this.maxLimit = 0;
        this.minLimit = 0;
        this.limit = new AtomicInteger(0);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M setCheck(BooleanSupplier checks) {
        return (M) super.setCheck(checks);
    }

    @Override
    public boolean isEmpty() {
        return cached.isEmpty();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public M cache(final boolean enableCache) {
        this.useCache = enableCache;
        return (M) this;
    }


    @Override
    public final int getMaxLimit() {
        return maxLimit;
    }


    @Override
    public final int getLimit() {
        return limit.get();
    }

    @Nonnull
    @Override
    public PaginationIterator<T> iterator() {
        return new PaginationIterator<>(cached, this::getNextChunk);
    }

    protected List<T> getRemainingCache() {
        int index = getIteratorIndex();
        if (useCache && index > -1 && index < cached.size())
            return cached.subList(index, cached.size());
        return Collections.emptyList();
    }

    public List<T> getNextChunk() {
        List<T> list = getRemainingCache();
        if (!list.isEmpty())
            return list;

        final int current = limit.getAndSet(getMaxLimit());
        list = complete();
        limit.set(current);
        return list;
    }

    protected abstract long getKey(T it);

    protected int getIteratorIndex() {
        for (int i = 0; i < cached.size(); i++) {
            if (getKey(cached.get(i)) == iteratorIndex)
                return i + 1;
        }
        return -1;
    }

    protected void updateIndex(T it) {
        long key = getKey(it);
        iteratorIndex = key;
        if (!useCache) {
            lastKey = key;
            last = it;
        }
    }

    protected class ChainedConsumer implements Consumer<List<T>> {
        protected final CompletableFuture<?> task;
        protected final Procedure<? super T> action;
        protected final Consumer<Throwable> throwableConsumer;
        protected boolean initial = true;

        protected ChainedConsumer(final CompletableFuture<?> task, final Procedure<? super T> action,
                                  final Consumer<Throwable> throwableConsumer) {
            this.task = task;
            this.action = action;
            this.throwableConsumer = throwableConsumer;
        }

        @Override
        public void accept(final List<T> list) {
            if (list.isEmpty() && !initial) {
                task.complete(null);
                return;
            }
            initial = false;

            T previous = null;
            for (T it : list) {
                if (task.isCancelled()) {
                    if (previous != null)
                        updateIndex(previous);
                    return;
                }
                if (action.execute(it)) {
                    previous = it;
                    continue;
                }
                // set the iterator index for next call of remaining
                updateIndex(it);
                task.complete(null);
                return;
            }

            final int currentLimit = limit.getAndSet(maxLimit);
            queue(this, throwableConsumer);
            limit.set(currentLimit);
        }
    }
}
