package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.internal.requests.RestAction;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;

import java.util.concurrent.CompletableFuture;

public class RestFuture<T> extends CompletableFuture<T> {
    private final Request<T> request;

    public RestFuture(final RestAction<T> restAction,
                      final boolean shouldQueue,
                      final RequestBody data,
                      final Route route) {
        this.request = new Request<>(restAction, this::complete, this::completeExceptionally, shouldQueue, data, route);
        restAction.getJDA().getRequester().request(this.request);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        if (this.request != null) {
            this.request.cancel();
        }

        return (!isDone() && !isCancelled()) && super.cancel(mayInterrupt);
    }
}
