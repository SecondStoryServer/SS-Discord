package me.syari.ss.discord.requests

import me.syari.ss.discord.exceptions.ErrorResponseException
import me.syari.ss.discord.exceptions.RateLimitedException
import me.syari.ss.discord.requests.CallbackContext.Companion.isCallbackContext
import okhttp3.RequestBody
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

internal open class RestAction<T>(
    private val route: Route, private val handler: ((Response, Request<T>) -> T)? = null
) {
    fun queue() {
        val data = finalizeData()
        val request = Request(this, { }, { }, true, data, route)
        Requester.request(request)
    }

    private fun submit(shouldQueue: Boolean): CompletableFuture<T> {
        val data = finalizeData()
        return RestFuture(this, shouldQueue, data, route)
    }

    fun complete(): T {
        return try {
            complete(true)
        } catch (ex: RateLimitedException) {
            throw AssertionError(ex)
        }
    }

    @Throws(RateLimitedException::class)
    fun complete(shouldQueue: Boolean): T {
        check(!isCallbackContext) { "Preventing use of complete() in callback threads! This operation can be a deadlock cause" }
        return try {
            submit(shouldQueue).get()
        } catch (ex: Throwable) {
            if (ex is ExecutionException) {
                val t = ex.cause
                if (t is RateLimitedException || t is ErrorResponseException) {
                    throw t
                }
            }
            throw RuntimeException(ex)
        }
    }

    open fun finalizeData(): RequestBody? {
        return null
    }

    open fun handleResponse(
        response: Response, request: Request<T>
    ) {
        if (response.isOk) {
            handleSuccess(response, request)
        } else {
            request.onFailure(response)
        }
    }

    open fun handleSuccess(
        response: Response, request: Request<T>
    ) {
        if (handler == null) {
            request.onSuccess(null)
        } else {
            request.onSuccess(handler.invoke(response, request))
        }
    }
}