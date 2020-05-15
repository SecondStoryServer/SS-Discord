

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CompletedRestAction<T> implements AuditableRestAction<T>
{
    private final JDA api;
    private final T value;
    private final Throwable error;

    public CompletedRestAction(JDA api, T value, Throwable error)
    {
        this.api = api;
        this.value = value;
        this.error = error;
    }

    public CompletedRestAction(JDA api, T value)
    {
        this(api, value, null);
    }

    public CompletedRestAction(JDA api, Throwable error)
    {
        this(api, null, error);
    }


    @Nonnull
    @Override
    public AuditableRestAction<T> reason(@Nullable String reason)
    {
        return this;
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return api;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> setCheck(@Nullable BooleanSupplier checks)
    {
        return this;
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure)
    {
        if (error == null)
        {
            if (success == null)
                RestAction.getDefaultSuccess().accept(value);
            else
                success.accept(value);
        }
        else
        {
            if (failure == null)
                RestAction.getDefaultFailure().accept(error);
            else
                failure.accept(error);
        }
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException
    {
        if (error != null)
        {
            if (error instanceof RateLimitedException)
                throw (RateLimitedException) error;
            if (error instanceof RuntimeException)
                throw (RuntimeException) error;
            if (error instanceof Error)
                throw (Error) error;
            throw new IllegalStateException(error);
        }
        return value;
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue)
    {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (error != null)
            future.completeExceptionally(error);
        else
            future.complete(value);
        return future;
    }
}
