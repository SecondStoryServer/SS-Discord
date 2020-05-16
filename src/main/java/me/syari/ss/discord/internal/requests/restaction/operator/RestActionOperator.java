package me.syari.ss.discord.internal.requests.restaction.operator;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.ContextException;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class RestActionOperator<I, O> implements RestAction<O> {
    protected final RestAction<I> action;

    public RestActionOperator(RestAction<I> action) {
        this.action = action;
    }

    protected <E> void doSuccess(Consumer<? super E> callback, E value) {
        if (callback == null)
            RestAction.getDefaultSuccess().accept(value);
        else
            callback.accept(value);
    }

    protected void doFailure(Consumer<? super Throwable> callback, Throwable throwable) {
        if (callback == null)
            RestAction.getDefaultFailure().accept(throwable);
        else
            callback.accept(throwable);
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return action.getJDA();
    }

    @Nonnull
    @Override
    public RestAction<O> setCheck(@Nullable BooleanSupplier checks) {
        action.setCheck(checks);
        return this;
    }

    protected Consumer<? super Throwable> contextWrap(@Nullable Consumer<? super Throwable> callback) {
        if (callback instanceof ContextException.ContextConsumer)
            return callback;
        else if (RestAction.isPassContext())
            return ContextException.here(callback == null ? RestAction.getDefaultFailure() : callback);
        return callback;
    }
}
