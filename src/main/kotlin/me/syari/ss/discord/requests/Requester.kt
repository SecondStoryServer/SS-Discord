package me.syari.ss.discord.requests

import me.syari.ss.discord.Discord
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HttpMethod.requiresRequestBody
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLPeerUnverifiedException

object Requester {
    private const val DISCORD_API_PREFIX = "https://discordapp.com/api/v6/"
    private val httpClient = OkHttpClient.Builder().build()
    private val EMPTY_BODY = ByteArray(0).toRequestBody()
    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

    fun <T> request(apiRequest: Request<T>) {
        if (apiRequest.shouldQueue) {
            RateLimiter.queueRequest(apiRequest)
        } else {
            execute(apiRequest, true)
        }
    }

    fun execute(apiRequest: Request<*>, handleOnRateLimit: Boolean): Long? {
        return execute(apiRequest, false, handleOnRateLimit)
    }

    private fun execute(
        apiRequest: Request<*>, retried: Boolean, handleOnRatelimit: Boolean
    ): Long? {
        val route = apiRequest.route
        var retryAfter: Long? = RateLimiter.getRateLimit(route)
        if (retryAfter != null && 0 < retryAfter) {
            if (handleOnRatelimit) apiRequest.handleResponse(Response(retryAfter))
            return retryAfter
        }
        val builder = okhttp3.Request.Builder()
        val url = DISCORD_API_PREFIX + route.route
        builder.url(url)
        val method = apiRequest.route.method.toString()
        var body = apiRequest.body
        if (body == null && requiresRequestBody(method)) body = EMPTY_BODY
        builder.apply {
            method(method, body)
            header("X-RateLimit-Precision", "millisecond")
            header("user-agent", "SS-Discord")
            header("accept-encoding", "gzip")
        }
        if (url.startsWith(DISCORD_API_PREFIX)) builder.header("authorization", "Bot ${Discord.token}")
        val request = builder.build()
        val responses = arrayOfNulls<okhttp3.Response>(4)
        var nullableLastResponse: okhttp3.Response? = null
        return try {
            var attempt = 0
            var lastResponse: okhttp3.Response
            do {
                val call = httpClient.newCall(request)
                lastResponse = call.execute()
                nullableLastResponse = lastResponse
                responses[attempt] = lastResponse
                if (lastResponse.code < 500) break
                attempt++
                try {
                    Thread.sleep(50 * attempt.toLong())
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            } while (attempt < 3 && 500 <= lastResponse.code)
            if (500 <= lastResponse.code) {
                val response = Response(lastResponse, -1)
                apiRequest.handleResponse(response)
                return null
            }
            retryAfter = RateLimiter.handleResponse(route, lastResponse)
            if (retryAfter == null) {
                apiRequest.handleResponse(Response(lastResponse, -1))
            } else if (handleOnRatelimit) {
                apiRequest.handleResponse(Response(lastResponse, retryAfter))
            }
            retryAfter
        } catch (ex: SocketTimeoutException) {
            if (!retried) return execute(apiRequest, true, handleOnRatelimit)
            apiRequest.handleResponse(Response(nullableLastResponse, ex))
            null
        } catch (ex: Exception) {
            if (!retried && (ex is SocketException || ex is SocketTimeoutException || ex is SSLPeerUnverifiedException)) {
                return execute(apiRequest, true, handleOnRatelimit)
            }
            apiRequest.handleResponse(Response(nullableLastResponse, ex))
            null
        } finally {
            for (response in responses) {
                response?.close()
            }
        }
    }
}