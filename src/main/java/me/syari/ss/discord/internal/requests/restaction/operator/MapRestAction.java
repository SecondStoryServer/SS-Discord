package me.syari.ss.discord.internal.requests.restaction.operator;

import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapRestAction<I, O> extends RestActionOperator<I, O> {
    private final Function<? super I, ? extends O> function;

    public MapRestAction(RestAction<I> action, Function<? super I, ? extends O> function) {
        super(action);
        this.function = function;
    }

    @Override
    public void queue(@Nullable Consumer<? super O> success, @Nullable Consumer<? super Throwable> failure) {
        action.queue((result) -> doSuccess(success, function.apply(result)), contextWrap(failure));
    }

    @Override
    public O complete(boolean shouldQueue) throws RateLimitedException {
        return function.apply(action.complete(shouldQueue));
    }

    @Nonnull
    @Override
    public CompletableFuture<O> submit(boolean shouldQueue) {
        return action.submit(shouldQueue).thenApply(function);
    }
}
