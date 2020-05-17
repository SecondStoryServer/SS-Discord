package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.exceptions.RateLimitedException;


public interface RestAction<T> {


    void queue();

    default T complete() {
        try {
            return complete(true);
        } catch (RateLimitedException e) {
            throw new AssertionError(e);
        }
    }


    T complete(boolean shouldQueue) throws RateLimitedException;


}
