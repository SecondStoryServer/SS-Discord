package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.concurrent.CompletableFuture;

public class RestFuture<T> extends CompletableFuture<T> {
    final Request<T> request;

    public RestFuture(final RestActionImpl<T> restAction, final boolean shouldQueue,
                      final RequestBody data,
                      final Route.CompiledRoute route, final CaseInsensitiveMap<String, String> headers) {
        this.request = new Request<T>(restAction, this::complete, this::completeExceptionally, shouldQueue, data, route, headers);
        ((JDAImpl) restAction.getJDA()).getRequester().request(this.request);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        if (this.request != null)
            this.request.cancel();

        return (!isDone() && !isCancelled()) && super.cancel(mayInterrupt);
    }
}
