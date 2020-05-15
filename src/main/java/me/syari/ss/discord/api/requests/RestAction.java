

package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.requests.restaction.MessageAction;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.ContextException;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.utils.concurrent.DelayedCompletableFuture;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.restaction.operator.DelayRestAction;
import me.syari.ss.discord.internal.requests.restaction.operator.FlatMapRestAction;
import me.syari.ss.discord.internal.requests.restaction.operator.MapRestAction;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.ContextRunnable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public interface RestAction<T>
{
    
    static void setPassContext(boolean enable)
    {
        RestActionImpl.setPassContext(enable);
    }

    
    static boolean isPassContext()
    {
        return RestActionImpl.isPassContext();
    }

    
    static void setDefaultFailure(@Nullable final Consumer<? super Throwable> callback)
    {
        RestActionImpl.setDefaultFailure(callback);
    }

    
    static void setDefaultSuccess(@Nullable final Consumer<Object> callback)
    {
        RestActionImpl.setDefaultSuccess(callback);
    }

    
    @Nonnull
    static Consumer<? super Throwable> getDefaultFailure()
    {
        return RestActionImpl.getDefaultFailure();
    }

    
    @Nonnull
    static Consumer<Object> getDefaultSuccess()
    {
        return RestActionImpl.getDefaultSuccess();
    }

    
    @Nonnull
    JDA getJDA();

    
    @Nonnull
    RestAction<T> setCheck(@Nullable BooleanSupplier checks);

    
    default void queue()
    {
        queue(null);
    }

    
    default void queue(@Nullable Consumer<? super T> success)
    {
        queue(success, null);
    }

    
    void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure);

    
    default T complete()
    {
        try
        {
            return complete(true);
        }
        catch (RateLimitedException e)
        {
            //This is so beyond impossible, but on the off chance that the laws of nature are rewritten
            // after the writing of this code, I'm placing this here.
            //Better safe than sorry?
            throw new AssertionError(e);
        }
    }

    
    T complete(boolean shouldQueue) throws RateLimitedException;

    
    @Nonnull
    default CompletableFuture<T> submit()
    {
        return submit(true);
    }

    
    @Nonnull
    CompletableFuture<T> submit(boolean shouldQueue);

    
    @Nonnull
    @CheckReturnValue
    default <O> RestAction<O> map(@Nonnull Function<? super T, ? extends O> map)
    {
        Checks.notNull(map, "Function");
        return new MapRestAction<>(this, map);
    }

    
    @Nonnull
    @CheckReturnValue
    default <O> RestAction<O> flatMap(@Nonnull Function<? super T, ? extends RestAction<O>> flatMap)
    {
        return flatMap(null, flatMap);
    }

    
    @Nonnull
    @CheckReturnValue
    default <O> RestAction<O> flatMap(@Nullable Predicate<? super T> condition, @Nonnull Function<? super T, ? extends RestAction<O>> flatMap)
    {
        Checks.notNull(flatMap, "Function");
        return new FlatMapRestAction<>(this, condition, flatMap);
    }

    
    @Nonnull
    @CheckReturnValue
    default RestAction<T> delay(@Nonnull Duration duration)
    {
        return delay(duration, null);
    }

    
    @Nonnull
    @CheckReturnValue
    default RestAction<T> delay(@Nonnull Duration duration, @Nullable ScheduledExecutorService scheduler)
    {
        Checks.notNull(duration, "Duration");
        return new DelayRestAction<>(this, TimeUnit.MILLISECONDS, duration.toMillis(), scheduler);
    }

    
    @Nonnull
    @CheckReturnValue
    default RestAction<T> delay(long delay, @Nonnull TimeUnit unit)
    {
        return delay(delay, unit, null);
    }

    
    @Nonnull
    @CheckReturnValue
    default RestAction<T> delay(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService scheduler)
    {
        Checks.notNull(unit, "TimeUnit");
        return new DelayRestAction<>(this, unit, delay, scheduler);
    }

    
    @Nonnull
    default DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit)
    {
        return submitAfter(delay, unit, null);
    }

    
    @Nonnull
    default DelayedCompletableFuture<T> submitAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor)
    {
        Checks.notNull(unit, "TimeUnit");
        if (executor == null)
            executor = getJDA().getRateLimitPool();
        return DelayedCompletableFuture.make(executor, delay, unit,
                (task) -> {
                    final Consumer<? super Throwable> onFailure;
                    if (isPassContext())
                        onFailure = ContextException.here(task::completeExceptionally);
                    else
                        onFailure = task::completeExceptionally;
                    return new ContextRunnable<T>(() -> queue(task::complete, onFailure));
                });
    }

    
    default T completeAfter(long delay, @Nonnull TimeUnit unit)
    {
        Checks.notNull(unit, "TimeUnit");
        try
        {
            unit.sleep(delay);
            return complete();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit)
    {
        return queueAfter(delay, unit, null, null, null);
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success)
    {
        return queueAfter(delay, unit, success, null, null);
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure)
    {
        return queueAfter(delay, unit, success, failure, null);
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable ScheduledExecutorService executor)
    {
        return queueAfter(delay, unit, null, null, executor);
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable ScheduledExecutorService executor)
    {
        return queueAfter(delay, unit, success, null, executor);
    }

    
    @Nonnull
    default ScheduledFuture<?> queueAfter(long delay, @Nonnull TimeUnit unit, @Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure, @Nullable ScheduledExecutorService executor)
    {
        Checks.notNull(unit, "TimeUnit");
        if (executor == null)
            executor = getJDA().getRateLimitPool();

        final Consumer<? super Throwable> onFailure;
        if (isPassContext())
            onFailure = ContextException.here(failure == null ? getDefaultFailure() : failure);
        else
            onFailure = failure;

        Runnable task = new ContextRunnable<Void>(() -> queue(success, onFailure));
        return executor.schedule(task, delay, unit);
    }
}
