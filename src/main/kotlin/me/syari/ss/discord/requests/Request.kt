package me.syari.ss.discord.requests

import me.syari.ss.discord.exceptions.ErrorResponseException.Companion.create
import me.syari.ss.discord.exceptions.RateLimitedException
import me.syari.ss.discord.requests.ErrorResponse.Companion.fromJSON
import me.syari.ss.discord.utils.ThreadingConfig
import okhttp3.RequestBody

class Request<T>(
    private val restAction: RestAction<T>,
    private val onSuccess: (T) -> Unit,
    private val onFailure: ((Throwable) -> Unit)?,
    val shouldQueue: Boolean,
    val body: RequestBody?,
    val route: Route
) {
    private val localReason = ThreadLocalReason.current
    var isCanceled = false
        private set

    fun onSuccess(successObj: T?) {
        if (successObj == null) return
        ThreadingConfig.callbackPool.execute {
            try {
                ThreadLocalReason.closable(localReason).use {
                    CallbackContext.instance.use { onSuccess.invoke(successObj) }
                }
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
        ThreadingConfig.callbackPool.execute {
            try {
                ThreadLocalReason.closable(localReason).use {
                    CallbackContext.instance.use {
                        onFailure?.let {
                            var cause: Throwable? = failException
                            while (cause?.cause != null) {
                                cause = cause.cause
                            }
                            cause?.initCause(Exception())
                            onFailure.invoke(failException)
                        }
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun cancel() {
        isCanceled = true
    }

    fun handleResponse(response: Response) {
        restAction.handleResponse(response, this)
    }
}