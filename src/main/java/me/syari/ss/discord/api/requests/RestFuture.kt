package me.syari.ss.discord.api.requests

import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route
import okhttp3.RequestBody
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

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
            Consumer { value: T -> complete(value) },
            Consumer { ex: Throwable -> completeExceptionally(ex) },
            shouldQueue,
            data,
            route
        )
        restAction.jda.requester.request(request)
    }
}