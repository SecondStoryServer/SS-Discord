package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.exceptions.ErrorResponseException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.exceptions.RateLimitedException;
import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.requests.Response;
import me.syari.ss.discord.api.requests.RestFuture;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RestAction<T> {
    public static final Logger LOG = JDALogger.getLog(RestAction.class);

    private static final Consumer<Object> DEFAULT_SUCCESS = o -> { };

    private static final Consumer<? super Throwable> DEFAULT_FAILURE = t ->
    {
        if (LOG.isDebugEnabled()) {
            LOG.error("RestAction queue returned failure", t);
        } else if (t.getCause() != null) {
            LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage(), t.getCause());
        } else {
            LOG.error("RestAction queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage());
        }
    };

    protected static final boolean passContext = true;
    protected final JDAImpl api;

    private final Route.CompiledRoute route;
    private final RequestBody data;
    private final BiFunction<Response, Request<T>, T> handler;

    public static boolean isPassContext() {
        return passContext;
    }

    public RestAction(@NotNull JDA api, Route.CompiledRoute route) {
        this(api, route, null, null);
    }

    public RestAction(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<T>, T> handler) {
        this(api, route, null, handler);
    }

    public RestAction(@NotNull JDA api, Route.CompiledRoute route, RequestBody data, BiFunction<Response, Request<T>, T> handler) {
        this.api = (JDAImpl) api;
        this.route = route;
        this.data = data;
        this.handler = handler;
    }

    @NotNull
    public JDA getJDA() {
        return api;
    }

    public void queue() {
        Route.CompiledRoute route = finalizeRoute();
        Checks.notNull(route, "Route");
        RequestBody data = finalizeData();
        api.getRequester().request(new Request<>(this, DEFAULT_SUCCESS, DEFAULT_FAILURE, true, data, route));
    }

    @NotNull
    private CompletableFuture<T> submit(boolean shouldQueue) {
        Route.CompiledRoute route = finalizeRoute();
        Checks.notNull(route, "Route");
        RequestBody data = finalizeData();
        return new RestFuture<>(this, shouldQueue, data, route);
    }

    public T complete() {
        try {
            return complete(true);
        } catch (RateLimitedException e) {
            throw new AssertionError(e);
        }
    }

    public T complete(boolean shouldQueue) throws RateLimitedException {
        if (CallbackContext.isCallbackContext()) {
            throw new IllegalStateException("Preventing use of complete() in callback threads! This operation can be a deadlock cause");
        }
        try {
            return submit(shouldQueue).get();
        } catch (Throwable e) {
            if (e instanceof ExecutionException) {
                Throwable t = e.getCause();
                if (t instanceof RateLimitedException) {
                    throw (RateLimitedException) t;
                } else if (t instanceof PermissionException) {
                    throw (PermissionException) t;
                } else if (t instanceof ErrorResponseException) {
                    throw (ErrorResponseException) t;
                }
            }
            throw new RuntimeException(e);
        }
    }

    protected RequestBody finalizeData() {
        return data;
    }

    protected Route.CompiledRoute finalizeRoute() {
        return route;
    }

    public void handleResponse(@NotNull Response response, Request<T> request) {
        if (response.isOk()) {
            handleSuccess(response, request);
        } else {
            request.onFailure(response);
        }
    }

    protected void handleSuccess(Response response, Request<T> request) {
        if (handler == null) {
            request.onSuccess(null);
        } else {
            request.onSuccess(handler.apply(response, request));
        }
    }
}
