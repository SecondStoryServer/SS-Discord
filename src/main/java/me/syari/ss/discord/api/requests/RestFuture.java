

package me.syari.ss.discord.api.requests;

import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

public class RestFuture<T> extends CompletableFuture<T>
{
    final Request<T> request;

    public RestFuture(final RestActionImpl<T> restAction, final boolean shouldQueue,
                      final BooleanSupplier checks, final RequestBody data, final Object rawData,
                      final Route.CompiledRoute route, final CaseInsensitiveMap<String, String> headers)
    {
        this.request = new Request<>(restAction, this::complete, this::completeExceptionally,
                                     checks, shouldQueue, data, rawData, route, headers);
        ((JDAImpl) restAction.getJDA()).getRequester().request(this.request);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt)
    {
        if (this.request != null)
            this.request.cancel();

        return (!isDone() && !isCancelled()) && super.cancel(mayInterrupt);
    }
}
