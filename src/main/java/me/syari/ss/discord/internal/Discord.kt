package me.syari.ss.discord.internal

import com.neovisionaries.ws.client.WebSocketFactory
import me.syari.ss.discord.api.MessageReceivedEvent
import me.syari.ss.discord.api.SessionController
import me.syari.ss.discord.api.data.DataObject
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import javax.security.auth.login.LoginException

object Discord {
    internal lateinit var token: String
    private lateinit var messageReceivedEvent: Discord.(MessageReceivedEvent) -> Unit

    @Throws(LoginException::class)
    fun init(token: String, messageReceivedEvent: Discord.(MessageReceivedEvent) -> Unit) {
        this.token = token
        this.messageReceivedEvent = messageReceivedEvent
        status = Status.INITIALIZED
        login()
    }

    private val shutdownHook = Thread(Runnable { shutdown() }, "JDA Shutdown Hook")
    val httpClient = OkHttpClient.Builder().build()
    val webSocketFactory = WebSocketFactory()
    var status = Status.INITIALIZING
        set(value) {
            synchronized(field) { field = value }
        }
    var responseTotal: Long = 0
        private set
    var gatewayUrl: String? = null
        private set

    @Throws(LoginException::class)
    fun login() {
        ThreadingConfig.init { "JDA" }
        RateLimiter.init()
        resetGatewayUrl()
        status = Status.LOGGING_IN
        verifyToken()
        WebSocketClient.init()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
    }

    @Throws(LoginException::class)
    fun verifyToken() {
        val login = object: RestAction<DataObject>(selfRoute) {
            override fun handleResponse(
                response: Response, request: Request<DataObject>
            ) {
                when {
                    response.isOk -> request.onSuccess(response.dataObject)
                    response.isRateLimit -> request.onFailure(RateLimitedException(request.route, response.retryAfter))
                    response.code == 401 -> request.onSuccess(null)
                    else -> request.onFailure(LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n$response"))
                }
            }
        }
        checkToken(login)
    }

    @Throws(LoginException::class)
    private fun checkToken(login: RestAction<DataObject>): DataObject {
        val userResponse: DataObject
        userResponse = try {
            login.complete()
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

    @Throws(InterruptedException::class)
    fun awaitReady() {
        awaitStatus(Status.CONNECTED)
    }

    val rateLimitPool: ScheduledExecutorService
        get() = ThreadingConfig.rateLimitPool

    val gatewayPool: ScheduledExecutorService
        get() = ThreadingConfig.gatewayPool

    val callbackPool: ExecutorService
        get() = ThreadingConfig.callbackPool

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

    fun setResponseTotal(responseTotal: Int) {
        this.responseTotal = responseTotal.toLong()
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
        SHUTDOWN(false);

    }
}