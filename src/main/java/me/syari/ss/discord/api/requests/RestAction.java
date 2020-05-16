package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.restaction.operator.MapRestAction;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;


public interface RestAction<T> {


    static boolean isPassContext() {
        return RestActionImpl.isPassContext();
    }


    @Nonnull
    static Consumer<? super Throwable> getDefaultFailure() {
        return RestActionImpl.getDefaultFailure();
    }


    @Nonnull
    static Consumer<Object> getDefaultSuccess() {
        return RestActionImpl.getDefaultSuccess();
    }


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
            //This is so beyond impossible, but on the off chance that the laws of nature are rewritten
            // after the writing of this code, I'm placing this here.
            //Better safe than sorry?
            throw new AssertionError(e);
        }
    }


    T complete(boolean shouldQueue) throws RateLimitedException;


    @Nonnull
    CompletableFuture<T> submit(boolean shouldQueue);


}
