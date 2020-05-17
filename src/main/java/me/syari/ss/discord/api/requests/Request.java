package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.api.audit.ThreadLocalReason;
import me.syari.ss.discord.api.exceptions.ContextException;
import me.syari.ss.discord.api.exceptions.ErrorResponseException;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.CallbackContext;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class Request<T> {
    private final JDAImpl api;
    private final RestActionImpl<T> restAction;
    private final Consumer<? super T> onSuccess;
    private final Consumer<? super Throwable> onFailure;
    private final boolean shouldQueue;
    private final Route.CompiledRoute route;
    private final RequestBody body;
    private final CaseInsensitiveMap<String, String> headers;

    private final String localReason;

    private boolean isCanceled = false;

    public Request(RestActionImpl<T> restAction,
                   Consumer<? super T> onSuccess,
                   Consumer<? super Throwable> onFailure,
                   boolean shouldQueue,
                   RequestBody body,
                   Route.CompiledRoute route,
                   CaseInsensitiveMap<String, String> headers) {
        this.restAction = restAction;
        this.onSuccess = onSuccess;
        if (onFailure instanceof ContextException.ContextConsumer)
            this.onFailure = onFailure;
        else if (RestActionImpl.isPassContext())
            this.onFailure = ContextException.here(onFailure);
        else
            this.onFailure = onFailure;
        this.shouldQueue = shouldQueue;
        this.body = body;
        this.route = route;
        this.headers = headers;

        this.api = (JDAImpl) restAction.getJDA();
        this.localReason = ThreadLocalReason.getCurrent();
    }

    public void onSuccess(T successObj) {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance()) {
                onSuccess.accept(successObj);
            } catch (Throwable t) {
                RestActionImpl.LOG.error("Encountered error while processing success consumer", t);
            }
        });
    }

    public void onFailure(Response response) {
        if (response.code == 429) {
            onFailure(new RateLimitedException(route, response.retryAfter));
        } else {
            onFailure(ErrorResponseException.create(
                    ErrorResponse.fromJSON(response.optObject().orElse(null)), response));
        }
    }

    public void onFailure(Throwable failException) {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance()) {
                onFailure.accept(failException);
            } catch (Throwable t) {
                RestActionImpl.LOG.error("Encountered error while processing failure consumer", t);
            }
        });
    }

    @Nullable
    public CaseInsensitiveMap<String, String> getHeaders() {
        return headers;
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
