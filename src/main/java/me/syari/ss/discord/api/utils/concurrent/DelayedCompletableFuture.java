

package me.syari.ss.discord.api.utils.concurrent;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.function.Function;


public class DelayedCompletableFuture<T> extends CompletableFuture<T> implements ScheduledFuture<T>
{
    private ScheduledFuture<?> future;

    private DelayedCompletableFuture() {}


    @Nonnull
    public static <E> DelayedCompletableFuture<E> make(@Nonnull ScheduledExecutorService executor, long delay, @Nonnull TimeUnit unit, @Nonnull Function<? super DelayedCompletableFuture<E>, ? extends Runnable> mapping)
    {
        DelayedCompletableFuture<E> handle = new DelayedCompletableFuture<>();
        ScheduledFuture<?> future = executor.schedule(mapping.apply(handle), delay, unit);
        handle.initProxy(future);
        return handle;
    }


    private void initProxy(ScheduledFuture<?> future)
    {
        if (this.future == null)
            this.future = future;
        else
            throw new IllegalStateException("Cannot initialize twice");
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        if (future != null && !future.isDone())
            future.cancel(mayInterruptIfRunning);
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public long getDelay(@Nonnull TimeUnit unit)
    {
        return future.getDelay(unit);
    }

    @Override
    public int compareTo(@Nonnull Delayed o)
    {
        return future.compareTo(o);
    }
}
