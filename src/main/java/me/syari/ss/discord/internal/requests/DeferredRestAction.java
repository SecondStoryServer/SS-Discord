package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DeferredRestAction<T, R extends RestAction<T>> implements AuditableRestAction<T> {
    private final JDA api;
    private final Class<T> type;
    private final Supplier<T> valueSupplier;
    private final Supplier<R> actionSupplier;

    private BooleanSupplier isAction;
    private BooleanSupplier transitiveChecks;

    public DeferredRestAction(JDA api, Class<T> type,
                              Supplier<T> valueSupplier,
                              Supplier<R> actionSupplier) {
        this.api = api;
        this.type = type;
        this.valueSupplier = valueSupplier;
        this.actionSupplier = actionSupplier;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return api;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> reason(String reason) {
        return this;
    }

    @Nonnull
    @Override
    public AuditableRestAction<T> setCheck(BooleanSupplier checks) {
        this.transitiveChecks = checks;
        return this;
    }

    @Override
    public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
        Consumer<? super T> finalSuccess;
        if (success != null)
            finalSuccess = success;
        else
            finalSuccess = RestAction.getDefaultSuccess();

        if (type == null) {
            BooleanSupplier checks = this.isAction;
            if (checks != null && checks.getAsBoolean())
                actionSupplier.get().queue(success, failure);
            else
                finalSuccess.accept(null);
            return;
        }

        T value = valueSupplier.get();
        if (value == null) {
            getAction().queue(success, failure);
        } else {
            finalSuccess.accept(value);
        }
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue) {
        if (type == null) {
            BooleanSupplier checks = this.isAction;
            if (checks != null && checks.getAsBoolean())
                return actionSupplier.get().submit(shouldQueue);
            return CompletableFuture.completedFuture(null);
        }
        T value = valueSupplier.get();
        if (value != null)
            return CompletableFuture.completedFuture(value);
        return getAction().submit(shouldQueue);
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException {
        if (type == null) {
            BooleanSupplier checks = this.isAction;
            if (checks != null && checks.getAsBoolean())
                return actionSupplier.get().complete(shouldQueue);
            return null;
        }
        T value = valueSupplier.get();
        if (value != null)
            return value;
        return getAction().complete(shouldQueue);
    }

    @SuppressWarnings("unchecked")
    private R getAction() {
        return (R) actionSupplier.get().setCheck(transitiveChecks);
    }
}
