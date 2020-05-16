package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.internal.requests.RestActionImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;


public interface RestAction<T> {


    @Nonnull
    RestAction<T> setCheck(@Nullable BooleanSupplier checks);


    default void queue() {
        queue(null);
    }


    default void queue(@Nullable Consumer<? super T> success) {
        queue(success, null);
    }


    void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure);


    default T complete() {
        try {
            return complete(true);
        } catch (RateLimitedException e) {
            throw new AssertionError(e);
        }
    }


    T complete(boolean shouldQueue) throws RateLimitedException;


    @Nonnull
    CompletableFuture<T> submit(boolean shouldQueue);


}
