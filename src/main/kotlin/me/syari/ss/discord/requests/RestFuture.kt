package me.syari.ss.discord.requests

import okhttp3.RequestBody
import java.util.concurrent.CompletableFuture

class RestFuture<T>(
    restAction: RestAction<T>, shouldQueue: Boolean, data: RequestBody?, route: Route
): CompletableFuture<T>() {
    private val request: Request<T>?
    override fun cancel(mayInterrupt: Boolean): Boolean {
        request?.cancel()
        return !isDone && !isCancelled && super.cancel(mayInterrupt)
    }

    init {
        request = Request(
            restAction,
            { complete(it) },
            { completeExceptionally(it) },
            shouldQueue,
            data,
            route
        )
        Requester.request(request)
    }
}