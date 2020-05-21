package me.syari.ss.discord.internal.requests

import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.internal.Discord
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http.HttpMethod.requiresRequestBody
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLPeerUnverifiedException

class Requester {
    val rateLimiter = RateLimiter(this)

    fun <T> request(apiRequest: Request<T>) {
        if (apiRequest.shouldQueue()) {
            rateLimiter.queueRequest(apiRequest)
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
        var retryAfter: Long? = rateLimiter.getRateLimit(route)
        if (retryAfter != null && retryAfter > 0) {
            if (handleOnRatelimit) apiRequest.handleResponse(me.syari.ss.discord.api.requests.Response(retryAfter))
            return retryAfter
        }
        val builder = okhttp3.Request.Builder()
        val url = DISCORD_API_PREFIX + route.route
        builder.url(url)
        val method = apiRequest.route.method.toString()
        var body = apiRequest.getBody()
        if (body == null && requiresRequestBody(method)) body = EMPTY_BODY
        builder.method(method, body).header("X-RateLimit-Precision", "millisecond").header("user-agent", USER_AGENT)
            .header("accept-encoding", "gzip")
        if (url.startsWith(DISCORD_API_PREFIX)) builder.header("authorization", "Bot ${Discord.token}")
        val request = builder.build()
        val responses = arrayOfNulls<Response>(4)
        var nullableLastResponse: Response? = null
        return try {
            var attempt = 0
            var lastResponse: Response
            do {
                val call = Discord.httpClient.newCall(request)
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
                val response = me.syari.ss.discord.api.requests.Response(lastResponse, -1)
                apiRequest.handleResponse(response)
                return null
            }
            retryAfter = rateLimiter.handleResponse(route, lastResponse)
            if (retryAfter == null) {
                apiRequest.handleResponse(me.syari.ss.discord.api.requests.Response(lastResponse, -1))
            } else if (handleOnRatelimit) {
                apiRequest.handleResponse(me.syari.ss.discord.api.requests.Response(lastResponse, retryAfter))
            }
            retryAfter
        } catch (e: SocketTimeoutException) {
            if (!retried) return execute(apiRequest, true, handleOnRatelimit)
            apiRequest.handleResponse(me.syari.ss.discord.api.requests.Response(nullableLastResponse, e))
            null
        } catch (e: Exception) {
            if (!retried && isRetry(e)) return execute(apiRequest, true, handleOnRatelimit)
            apiRequest.handleResponse(me.syari.ss.discord.api.requests.Response(nullableLastResponse, e))
            null
        } finally {
            for (response in responses) {
                if (response == null) break
                response.close()
            }
        }
    }

    fun shutdown() {
        rateLimiter.shutdown()
    }

    companion object {
        const val DISCORD_API_PREFIX = "https://discordapp.com/api/v6/"
        const val USER_AGENT = "SS-Discord"
        private val EMPTY_BODY = ByteArray(0).toRequestBody()
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        private fun isRetry(ex: Throwable): Boolean {
            return ex is SocketException || ex is SocketTimeoutException || ex is SSLPeerUnverifiedException
        }
    }

}