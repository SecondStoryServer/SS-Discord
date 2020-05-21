package me.syari.ss.discord.internal

import com.neovisionaries.ws.client.WebSocketFactory
import me.syari.ss.discord.api.MessageReceivedEvent
import me.syari.ss.discord.api.SessionController
import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.api.exceptions.RateLimitedException
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.internal.entities.EntityBuilder
import me.syari.ss.discord.internal.entities.Message
import me.syari.ss.discord.internal.handle.EventCache
import me.syari.ss.discord.internal.handle.GuildSetupController
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

class JDA(token: String, private val messageReceivedEvent: MessageReceivedEvent.() -> Unit) {
    private val shutdownHook = Thread(Runnable { shutdown() }, "JDA Shutdown Hook")
    val entityBuilder = EntityBuilder(this)
    val eventCache = EventCache()
    val guildSetupController = GuildSetupController(this)
    val token: String = "Bot $token"
    private val threadConfig = ThreadingConfig()
    val sessionController = SessionController()
    val httpClient = OkHttpClient.Builder().build()
    val webSocketFactory = WebSocketFactory()
    lateinit var client: WebSocketClient
        private set
    val requester = Requester(this)
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
        threadConfig.init { "JDA" }
        requester.rateLimiter.init()
        gatewayUrl = gateway
        status = Status.LOGGING_IN
        verifyToken()
        client = WebSocketClient(this)
        Runtime.getRuntime().addShutdownHook(shutdownHook)
    }

    private val gateway: String
        get() = sessionController.getGateway(this)

    @Throws(LoginException::class)
    fun verifyToken() {
        val login: RestAction<DataObject> = object: RestAction<DataObject>(this@JDA, selfRoute) {
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
        get() = threadConfig.rateLimitPool

    val gatewayPool: ScheduledExecutorService
        get() = threadConfig.gatewayPool

    val callbackPool: ExecutorService
        get() = threadConfig.callbackPool

    @Synchronized
    fun shutdown() {
        if (status == Status.SHUTDOWN || status == Status.SHUTTING_DOWN) return
        status = Status.SHUTTING_DOWN
        client.shutdown()
        shutdownInternals()
    }

    fun callMessageReceiveEvent(message: Message) {
        messageReceivedEvent.invoke(MessageReceivedEvent(message))
    }

    @Synchronized
    fun shutdownInternals() {
        if (status == Status.SHUTDOWN) return
        requester.shutdown()
        threadConfig.shutdown()
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
        gatewayUrl = gateway
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

    companion object {
        @Throws(LoginException::class)
        fun build(token: String, messageReceivedEvent: MessageReceivedEvent.() -> Unit): JDA {
            val jda = JDA(token, messageReceivedEvent)
            jda.status = Status.INITIALIZED
            jda.login()
            return jda
        }
    }

}