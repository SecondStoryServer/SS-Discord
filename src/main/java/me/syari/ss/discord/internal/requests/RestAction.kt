package me.syari.ss.discord.internal.requests

import me.syari.ss.discord.api.exceptions.ErrorResponseException
import me.syari.ss.discord.api.exceptions.RateLimitedException
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.api.requests.RestFuture
import me.syari.ss.discord.internal.requests.CallbackContext.Companion.isCallbackContext
import okhttp3.RequestBody
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

open class RestAction<T>(
    private val route: Route, private val handler: ((Response, Request<T>) -> T)? = null
) {
    fun queue() {
        val data = finalizeData()
        val request = Request(this, DEFAULT_SUCCESS, DEFAULT_FAILURE, true, data, route)
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

    private fun finalizeRoute(): Route? {
        return route
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

    companion object {
        private val DEFAULT_SUCCESS = Consumer { _: Any? -> }
        private val DEFAULT_FAILURE: Consumer<in Throwable> = Consumer { }
    }
}