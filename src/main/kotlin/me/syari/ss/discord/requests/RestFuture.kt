package me.syari.ss.discord.requests

import okhttp3.RequestBody
import java.util.concurrent.CompletableFuture

internal class RestFuture<T>(
    restAction: RestAction<T>, shouldQueue: Boolean, data: RequestBody?, route: Route
): CompletableFuture<T>() {
    private val request: Request<T> =
        Request(restAction, { complete(it) }, { completeExceptionally(it) }, shouldQueue, data, route
        )

    override fun cancel(mayInterrupt: Boolean): Boolean {
        request.cancel()
        return !isDone && !isCancelled && super.cancel(mayInterrupt)
    }

    init {
        Requester.request(request)
    }
}