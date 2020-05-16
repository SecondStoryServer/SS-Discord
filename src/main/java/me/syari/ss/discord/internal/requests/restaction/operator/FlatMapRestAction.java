package me.syari.ss.discord.internal.requests.restaction.operator;

import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlatMapRestAction<I, O> extends RestActionOperator<I, O> {
    private final Function<? super I, ? extends RestAction<O>> function;
    private final Predicate<? super I> condition;

    public FlatMapRestAction(RestAction<I> action, Predicate<? super I> condition,
                             Function<? super I, ? extends RestAction<O>> function) {
        super(action);
        this.function = function;
        this.condition = condition;
    }

    @Override
    public void queue(@Nullable Consumer<? super O> success, @Nullable Consumer<? super Throwable> failure) {
        Consumer<? super Throwable> onFailure = contextWrap(failure);
        action.queue((result) -> {
            if (condition != null && !condition.test(result))
                return;
            RestAction<O> then = function.apply(result);
            if (then == null)
                doFailure(onFailure, new IllegalStateException("FlatMap operand is null"));
            else
                then.queue(success, onFailure);
        }, onFailure);
    }

    @Override
    public O complete(boolean shouldQueue) throws RateLimitedException {
        return function.apply(action.complete(shouldQueue)).complete(shouldQueue);
    }

    @Nonnull
    @Override
    public CompletableFuture<O> submit(boolean shouldQueue) {
        return action.submit(shouldQueue)
                .thenCompose((result) -> function.apply(result).submit(shouldQueue));
    }
}
