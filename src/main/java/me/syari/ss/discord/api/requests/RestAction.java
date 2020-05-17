package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.exceptions.RateLimitedException;

import javax.annotation.Nullable;
import java.util.function.Consumer;


public interface RestAction<T> {


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


}
