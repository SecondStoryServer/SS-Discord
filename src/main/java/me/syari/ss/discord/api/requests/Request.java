package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.ThreadLocalReason;
import me.syari.ss.discord.api.exceptions.ContextException;
import me.syari.ss.discord.api.exceptions.ErrorResponseException;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.requests.CallbackContext;
import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Request<T> {
    private final RestAction<T> restAction;
    private final Consumer<? super T> onSuccess;
    private final Consumer<? super Throwable> onFailure;
    private final boolean shouldQueue;
    private final RequestBody body;
    private final Route.CompiledRoute route;
    private final JDA api;
    private final String localReason = ThreadLocalReason.getCurrent();
    private boolean isCanceled = false;

    public Request(RestAction<T> restAction,
                   Consumer<? super T> onSuccess,
                   Consumer<? super Throwable> onFailure,
                   boolean shouldQueue,
                   RequestBody body,
                   Route.CompiledRoute route) {
        this.restAction = restAction;
        this.onSuccess = onSuccess;
        if (onFailure instanceof ContextException.ContextConsumer) {
            this.onFailure = onFailure;
        } else {
            this.onFailure = ContextException.from(onFailure);
        }
        this.shouldQueue = shouldQueue;
        this.body = body;
        this.route = route;
        this.api = restAction.getJDA();
    }

    public void onSuccess(T successObj) {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance()) {
                onSuccess.accept(successObj);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onFailure(@NotNull Response response) {
        if (response.code == 429) {
            onFailure(new RateLimitedException(route, response.retryAfter));
        } else {
            onFailure(ErrorResponseException.create(ErrorResponse.fromJSON(response.optObject().orElse(null)), response));
        }
    }

    public void onFailure(Throwable failException) {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance()) {
                onFailure.accept(failException);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @NotNull
    public Route.CompiledRoute getRoute() {
        return route;
    }

    @Nullable
    public RequestBody getBody() {
        return body;
    }

    public boolean shouldQueue() {
        return shouldQueue;
    }

    public void cancel() {
        this.isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void handleResponse(@NotNull Response response) {
        restAction.handleResponse(response, this);
    }
}
