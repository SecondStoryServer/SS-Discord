package me.syari.ss.discord.api.requests

import me.syari.ss.discord.api.ThreadLocalReason
import me.syari.ss.discord.api.exceptions.ContextException.ContextConsumer
import me.syari.ss.discord.api.exceptions.ContextException.from
import me.syari.ss.discord.api.exceptions.ErrorResponseException.Companion.create
import me.syari.ss.discord.api.exceptions.RateLimitedException
import me.syari.ss.discord.api.requests.ErrorResponse.Companion.fromJSON
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.requests.CallbackContext
import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route
import okhttp3.RequestBody
import java.util.function.Consumer

class Request<T>(
    private val restAction: RestAction<T>,
    private val onSuccess: Consumer<in T>,
    onFailure: Consumer<in Throwable>?,
    shouldQueue: Boolean,
    body: RequestBody?,
    route: Route
) {
    private var onFailure: Consumer<in Throwable>? = null
    private val shouldQueue: Boolean
    private val body: RequestBody?
    val route: Route
    private val api: JDA
    private val localReason = ThreadLocalReason.current
    var isCanceled = false
        private set

    fun onSuccess(successObj: T?) {
        if(successObj == null) return
        api.callbackPool.execute {
            try {
                ThreadLocalReason.closable(localReason).use { CallbackContext.instance.use { onSuccess.accept(successObj) } }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    fun onFailure(response: Response) {
        if (response.code == 429) {
            onFailure(RateLimitedException(route, response.retryAfter))
        } else {
            onFailure(create(fromJSON(response.optObject()), response))
        }
    }

    fun onFailure(failException: Throwable) {
        api.callbackPool.execute {
            try {
                ThreadLocalReason.closable(localReason).use {
                    CallbackContext.instance.use { onFailure?.accept(failException) }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun getBody(): RequestBody? {
        return body
    }

    fun shouldQueue(): Boolean {
        return shouldQueue
    }

    fun cancel() {
        isCanceled = true
    }

    fun handleResponse(response: Response) {
        restAction.handleResponse(response, this)
    }

    init {
        this.onFailure = if (onFailure is ContextConsumer) {
            onFailure
        } else {
            onFailure?.let { from(it) }
        }
        this.shouldQueue = shouldQueue
        this.body = body
        this.route = route
        api = restAction.jda
    }
}