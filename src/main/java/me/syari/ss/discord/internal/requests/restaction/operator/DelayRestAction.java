

package me.syari.ss.discord.internal.requests.restaction.operator;

import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DelayRestAction<T> extends RestActionOperator<T, T>
{
    private final TimeUnit unit;
    private final long delay;
    private final ScheduledExecutorService scheduler;

    public DelayRestAction(RestAction<T> action, TimeUnit unit, long delay, ScheduledExecutorService scheduler)
    {
        super(action);
        this.unit = unit;
        this.delay = delay;
        this.scheduler = scheduler == null ? action.getJDA().getRateLimitPool() : scheduler;
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure)
    {
        action.queue((result) ->
            scheduler.schedule(() ->
                doSuccess(success, result),
            delay, unit),
        contextWrap(failure));
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException
    {
        T result = action.complete(shouldQueue);
        try
        {
            unit.sleep(delay);
            return result;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue)
    {
        CompletableFuture<T> future = new CompletableFuture<>();
        queue(future::complete, future::completeExceptionally);
        return future;
    }
}
