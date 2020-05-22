package me.syari.ss.discord.internal

import com.neovisionaries.ws.client.WebSocketFactory
import me.syari.ss.discord.api.MessageReceivedEvent
import me.syari.ss.discord.api.SessionController
import me.syari.ss.discord.api.data.DataContainer
import me.syari.ss.discord.api.exceptions.RateLimitedException
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.internal.entities.Message
import me.syari.ss.discord.internal.requests.RateLimiter
import me.syari.ss.discord.internal.requests.Requester
import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route.Companion.selfRoute
import me.syari.ss.discord.internal.requests.WebSocketClient
import me.syari.ss.discord.internal.utils.ThreadingConfig
import okhttp3.OkHttpClient
import java.util.concurrent.ExecutionException
import javax.security.auth.login.LoginException

object Discord {
    internal lateinit var token: String
    private lateinit var messageReceivedEvent: Discord.(MessageReceivedEvent) -> Unit

    @Throws(LoginException::class, InterruptedException::class)
    fun init(token: String, messageReceivedEvent: Discord.(MessageReceivedEvent) -> Unit) {
        this.token = token
        this.messageReceivedEvent = messageReceivedEvent
        status = Status.INITIALIZED
        login()
        awaitStatus(Status.CONNECTED)
    }

    private val shutdownHook = Thread(Runnable { shutdown() }, "SS-Discord Shutdown Hook")
    val httpClient = OkHttpClient.Builder().build()
    val webSocketFactory = WebSocketFactory()
    var status = Status.INITIALIZING
        set(value) {
            synchronized(field) { field = value }
        }
    var responseTotal: Int = 0
    var gatewayUrl: String? = null
        private set

    @Throws(LoginException::class)
    private fun login() {
        ThreadingConfig.init()
        RateLimiter.init()
        resetGatewayUrl()
        status = Status.LOGGING_IN
        verifyToken()
        WebSocketClient.init()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
    }

    @Throws(LoginException::class)
    private fun verifyToken() {
        checkToken { response, request ->
            when {
                response.isOk -> request.onSuccess(response.dataObject)
                response.isRateLimit -> request.onFailure(RateLimitedException(request.route, response.retryAfter))
                response.code == 401 -> request.onSuccess(null)
                else -> request.onFailure(LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n$response"))
            }
        }
    }

    @Throws(LoginException::class)
    private fun checkToken(run: (Response, Request<DataContainer>) -> Unit): DataContainer {
        val userResponse: DataContainer
        userResponse = try {
            object: RestAction<DataContainer>(selfRoute) {
                override fun handleResponse(response: Response, request: Request<DataContainer>) {
                    run.invoke(response, request)
                }
            }.complete()
        } catch (ex: RuntimeException) {
            val cause = ex.cause
            val throwable = if (cause is ExecutionException) cause.cause else null
            if (throwable is LoginException) {
                throw LoginException(throwable.message)
            } else {
                throw ex
            }
        }
        return userResponse
    }

    @Throws(InterruptedException::class)
    fun awaitStatus(
        status: Status, vararg failOn: Status
    ) {
        require(status.isInit) { "Cannot await the status $status as it is not part of the login cycle!" }
        if (status == Status.CONNECTED) return
        val failStatus = listOf(*failOn)
        while (!status.isInit || status.ordinal < status.ordinal) {
            check(status != Status.SHUTDOWN) { "Was shutdown trying to await status" }
            if (failStatus.contains(status)) {
                return
            } else {
                Thread.sleep(50)
            }
        }
    }

    @Synchronized
    fun shutdown() {
        if (status == Status.SHUTDOWN || status == Status.SHUTTING_DOWN) return
        status = Status.SHUTTING_DOWN
        WebSocketClient.shutdown()
        shutdownInternals()
    }

    fun callMessageReceiveEvent(message: Message) {
        messageReceivedEvent.invoke(this, MessageReceivedEvent(message))
    }

    @Synchronized
    fun shutdownInternals() {
        if (status == Status.SHUTDOWN) return
        Requester.shutdown()
        ThreadingConfig.shutdown()
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        status = Status.SHUTDOWN
    }

    fun resetGatewayUrl() {
        gatewayUrl = SessionController.getGateway()
    }

    enum class Status(val isInit: Boolean) {
        INITIALIZING(true),
        INITIALIZED(true),
        LOGGING_IN(true),
        CONNECTING_TO_WEBSOCKET(true),
        IDENTIFYING_SESSION(true),
        AWAITING_LOGIN_CONFIRMATION(true),
        LOADING_SUBSYSTEMS(true),
        CONNECTED(true),
        DISCONNECTED(false),
        RECONNECT_QUEUED(false),
        WAITING_TO_RECONNECT(false),
        ATTEMPTING_TO_RECONNECT(false),
        SHUTTING_DOWN(false),
        SHUTDOWN(false)
    }
}