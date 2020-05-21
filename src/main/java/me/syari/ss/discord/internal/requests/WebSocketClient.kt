package me.syari.ss.discord.internal.requests

import com.neovisionaries.ws.client.ThreadType
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFrame
import com.neovisionaries.ws.client.WebSocketListener
import me.syari.ss.discord.api.SessionController
import me.syari.ss.discord.api.SessionController.SessionConnectNode
import me.syari.ss.discord.api.data.DataArray
import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.api.requests.CloseCode.Companion.from
import me.syari.ss.discord.internal.Discord
import me.syari.ss.discord.internal.handle.EventCache
import me.syari.ss.discord.internal.handle.GuildCreateHandler
import me.syari.ss.discord.internal.handle.GuildSetupController
import me.syari.ss.discord.internal.handle.MessageCreateHandler
import me.syari.ss.discord.internal.utils.ThreadingConfig
import me.syari.ss.discord.internal.utils.ZlibDecompressor
import java.io.IOException
import java.net.URI
import java.util.Objects
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.zip.DataFormatException

object WebSocketClient: WebSocketAdapter(), WebSocketListener {
    private const val DISCORD_GATEWAY_VERSION = 6
    private const val INVALIDATE_REASON = "INVALIDATE_SESSION"

    private val IDENTIFY_BACKOFF = TimeUnit.SECONDS.toMillis(SessionController.IDENTIFY_DELAY.toLong())
    private var socket: WebSocket? = null
    private var sessionId: String? = null
    private val readLock = Any()
    private var ratelimitThread: WebSocketSendingThread? = null

    @Volatile
    private var keepAliveThread: Future<*>? = null
    private var initiating = false
    private var reconnectTimeoutS = 2
    private var identifyTime: Long = 0

    @Volatile
    private var ratelimitResetTime: Long = 0
    private val messagesSent = AtomicInteger(0)

    @Volatile
    private var shutdown = false
    private var shouldReconnect = true
    private var handleIdentifyRateLimit = false
    private var connected = false
    private var processingReady = true

    @Volatile
    private var connectNode: SessionConnectNode? = null

    val executor = ThreadingConfig.gatewayPool
    val queueLock = ReentrantLock()
    val chunkSyncQueue: Queue<String> = ConcurrentLinkedQueue()
    val ratelimitQueue: Queue<String> = ConcurrentLinkedQueue()

    @Volatile
    var sentAuthInfo = false

    fun init() {
        connectNode = StartingNode().apply {
            try {
                SessionController.appendSession(this)
            } catch (ex: RuntimeException) {
                Discord.status = Discord.Status.SHUTDOWN
                throw ex
            } catch (ex: Error) {
                Discord.status = Discord.Status.SHUTDOWN
                throw ex
            }
        }
    }

    fun ready() {
        if (initiating) {
            initiating = false
            processingReady = false
        }
        Discord.status = Discord.Status.CONNECTED
    }

    val isReady: Boolean
        get() = !initiating

    fun handle(events: List<DataObject>) {
        events.forEach(Consumer { raw: DataObject -> onDispatch(raw) })
    }

    fun chunkOrSyncRequest(request: DataObject) {
        locked(Supplier { chunkSyncQueue.add(request.toString()) })
    }

    fun send(message: String?, skipQueue: Boolean): Boolean {
        if (!connected) return false
        val now = System.currentTimeMillis()
        if (ratelimitResetTime <= now) {
            messagesSent.set(0)
            ratelimitResetTime = now + 60000
        }
        return if (messagesSent.get() <= 115 || skipQueue && messagesSent.get() <= 119) {
            socket?.sendText(message)
            messagesSent.getAndIncrement()
            true
        } else {
            false
        }
    }

    fun close() {
        socket?.sendClose(1000)
    }

    private fun close(code: Int, reason: String?) {
        socket?.sendClose(code, reason)
    }

    @Synchronized
    fun shutdown() {
        shutdown = true
        shouldReconnect = false
        connectNode?.let { SessionController.removeSession(it) }
        close(1000, "Shutting down")
    }

    @Synchronized
    private fun connect() {
        if (Discord.status !== Discord.Status.ATTEMPTING_TO_RECONNECT) Discord.status =
            Discord.Status.CONNECTING_TO_WEBSOCKET
        if (shutdown) throw RejectedExecutionException("SS-Discord is shutdown!")
        initiating = true
        val url = Discord.gatewayUrl + "?encoding=json&v=" + DISCORD_GATEWAY_VERSION + "&compress=zlib-stream"
        try {
            val socketFactory = Discord.webSocketFactory
            val notNullSocket: WebSocket
            synchronized(socketFactory) {
                val host = URI.create(url).host
                if (host != null) {
                    socketFactory.setServerName(host)
                } else {
                    socketFactory.serverNames = null
                }
                notNullSocket = socketFactory.createSocket(url)
                socket = notNullSocket
            }
            notNullSocket.addHeader("Accept-Encoding", "gzip").addListener(this).connect()
        } catch (ex: IOException) {
            Discord.resetGatewayUrl()
            throw IllegalStateException(ex)
        } catch (ex: WebSocketException) {
            Discord.resetGatewayUrl()
            throw IllegalStateException(ex)
        }
    }

    override fun onThreadStarted(
        websocket: WebSocket, threadType: ThreadType, thread: Thread
    ) {
    }

    override fun onConnected(
        websocket: WebSocket, headers: Map<String, List<String>>
    ) {
        Discord.status = Discord.Status.IDENTIFYING_SESSION
        connected = true
        messagesSent.set(0)
        ratelimitResetTime = System.currentTimeMillis() + 60000
        if (sessionId == null) {
            sendIdentify()
        } else {
            sendResume()
        }
    }

    override fun onDisconnected(
        websocket: WebSocket,
        serverCloseFrame: WebSocketFrame,
        clientCloseFrame: WebSocketFrame,
        closedByServer: Boolean
    ) {
        sentAuthInfo = false
        connected = false
        Discord.status = Discord.Status.DISCONNECTED
        var isInvalidate = false
        keepAliveThread?.apply {
            cancel(false)
            keepAliveThread = null
        }
        val rawCloseCode = serverCloseFrame.closeCode
        val closeCode = from(rawCloseCode)
        if (clientCloseFrame.closeCode == 1000 && clientCloseFrame.closeReason == INVALIDATE_REASON) {
            isInvalidate = true
        }
        val closeCodeIsReconnect = closeCode == null || closeCode.isReconnect
        if (!shouldReconnect || !closeCodeIsReconnect || executor.isShutdown) {
            ratelimitThread?.apply {
                shutdown()
                ratelimitThread = null
            }
            ZlibDecompressor.reset()
            Discord.shutdownInternals()
        } else {
            synchronized(readLock) { ZlibDecompressor.reset() }
            if (isInvalidate) invalidate()
            try {
                handleReconnect()
            } catch (ex: InterruptedException) {
                invalidate()
                queueReconnect()
            }
        }
    }

    @Throws(InterruptedException::class)
    private fun handleReconnect() {
        if (sessionId == null) {
            queueReconnect()
        } else {
            reconnect()
        }
    }

    private fun queueReconnect() {
        try {
            Discord.status = Discord.Status.RECONNECT_QUEUED
            connectNode = ReconnectNode().apply {
                SessionController.appendSession(this)
            }
        } catch (ex: IllegalStateException) {
            Discord.status = Discord.Status.SHUTDOWN
        }
    }

    @Throws(InterruptedException::class)
    private fun reconnect() {
        if (shutdown) {
            Discord.status = Discord.Status.SHUTDOWN
            return
        }
        while (shouldReconnect) {
            Discord.status = Discord.Status.WAITING_TO_RECONNECT
            val delay = reconnectTimeoutS
            reconnectTimeoutS = (reconnectTimeoutS shl 1).coerceAtMost(900)
            Thread.sleep(delay * 1000.toLong())
            handleIdentifyRateLimit = false
            Discord.status = Discord.Status.ATTEMPTING_TO_RECONNECT
            try {
                connect()
                break
            } catch (ex: RejectedExecutionException) {
                Discord.status = Discord.Status.SHUTDOWN
                return
            } catch (ex: RuntimeException) {
                ex.printStackTrace()
            }
        }
    }

    private fun setupKeepAlive(timeout: Long) {
        keepAliveThread = executor.scheduleAtFixedRate(
            { if (connected) sendKeepAlive() }, 0, timeout, TimeUnit.MILLISECONDS
        )
    }

    private fun sendKeepAlive() {
        val keepAlivePacket =
            DataObject.empty().put("op", WebSocketCode.HEARTBEAT).put("d", Discord.responseTotal).toString()
        send(keepAlivePacket, true)
    }

    private fun sendIdentify() {
        val connectionProperties =
            DataObject.empty().put("\$os", System.getProperty("os.name")).put("\$browser", "SS-Discord").put("\$device", "SS-Discord")
                .put("\$referring_domain", "").put("\$referrer", "")
        val payload = DataObject.empty().put("token", token).put("properties", connectionProperties)
            .put("v", DISCORD_GATEWAY_VERSION).put("guild_subscriptions", true).put("large_threshold", 250)
        val identify = DataObject.empty().put("op", WebSocketCode.IDENTIFY).put("d", payload)
        payload.put(
            "shard", DataArray.empty().add(0).add(1)
        )
        send(identify.toString(), true)
        handleIdentifyRateLimit = true
        identifyTime = System.currentTimeMillis()
        sentAuthInfo = true
        Discord.status = Discord.Status.AWAITING_LOGIN_CONFIRMATION
    }

    private fun sendResume() {
        val resume = DataObject.empty().put("op", WebSocketCode.RESUME).put(
            "d", DataObject.empty().put("session_id", sessionId).put("token", token).put("seq", Discord.responseTotal)
        )
        send(resume.toString(), true)
        Discord.status = Discord.Status.AWAITING_LOGIN_CONFIRMATION
    }

    private fun invalidate() {
        sessionId = null
        sentAuthInfo = false
        locked(Runnable { chunkSyncQueue.clear() })
        EventCache.clear()
        GuildSetupController.clearCache()
    }

    private val token: String
        get() = Discord.token

    private fun handleEvent(content: DataObject) {
        try {
            onEvent(content)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun onEvent(content: DataObject) {
        val opCode = content.getInt("op")
        if (!content.isNull("s")) Discord.setResponseTotal(content.getInt("s"))
        when (opCode) {
            WebSocketCode.DISPATCH -> onDispatch(content)
            WebSocketCode.HEARTBEAT -> sendKeepAlive()
            WebSocketCode.RECONNECT -> close(4000, "OP 7: RECONNECT")
            WebSocketCode.INVALIDATE_SESSION -> {
                handleIdentifyRateLimit =
                    handleIdentifyRateLimit && System.currentTimeMillis() - identifyTime < IDENTIFY_BACKOFF
                sentAuthInfo = false
                val isResume = content.getBoolean("d", false)
                val closeCode = if (isResume) 4000 else 1000
                if (!isResume) {
                    invalidate()
                }
                close(closeCode, INVALIDATE_REASON)
            }
            WebSocketCode.HELLO -> {
                val data = content.getObject("d")
                setupKeepAlive(data.getLong("heartbeat_interval"))
            }
        }
    }

    private fun onDispatch(raw: DataObject) {
        val type = raw.getString("t")
        val responseTotal = Discord.responseTotal
        if (raw["d"] !is Map<*, *>) {
            return
        }
        val content = raw.getObject("d")
        try {
            when (type) {
                "READY" -> {
                    reconnectTimeoutS = 2
                    Discord.status = Discord.Status.LOADING_SUBSYSTEMS
                    processingReady = true
                    handleIdentifyRateLimit = false
                    sessionId = content.getString("session_id")
                }
                "RESUMED" -> {
                    reconnectTimeoutS = 2
                    sentAuthInfo = true
                    if (!processingReady) {
                        initiating = false
                        ready()
                    } else {
                        Discord.status = Discord.Status.LOADING_SUBSYSTEMS
                    }
                }
                "GUILD_CREATE" -> GuildCreateHandler.handle(responseTotal, raw)
                "MESSAGE_CREATE" -> MessageCreateHandler.handle(responseTotal, raw)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        if (responseTotal % EventCache.TIMEOUT_AMOUNT == 0L) EventCache.timeout(responseTotal)
    }

    @Throws(DataFormatException::class)
    override fun onBinaryMessage(
        websocket: WebSocket, binary: ByteArray
    ) {
        val json = synchronized(readLock) { handleBinary(binary) }
        json?.let { handleEvent(it) }
    }

    @Throws(DataFormatException::class)
    private fun handleBinary(binary: ByteArray): DataObject? {
        val json: String?
        try {
            json = ZlibDecompressor.decompress(binary)
            if (json == null) return null
        } catch (ex: DataFormatException) {
            close(4000, "MALFORMED_PACKAGE")
            throw ex
        }
        return DataObject.fromJson(json)
    }

    fun maybeUnlock() {
        if (queueLock.isHeldByCurrentThread) queueLock.unlock()
    }

    private fun locked(task: Runnable) {
        try {
            queueLock.lockInterruptibly()
            task.run()
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        } finally {
            maybeUnlock()
        }
    }

    private fun <T> locked(task: Supplier<T>) {
        try {
            queueLock.lockInterruptibly()
            task.get()
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        } finally {
            maybeUnlock()
        }
    }

    private class StartingNode: SessionConnectNode {
        @Throws(InterruptedException::class)
        override fun run(isLast: Boolean) {
            if (shutdown) return
            ratelimitThread = WebSocketSendingThread().apply {
                start()
            }
            connect()
            if (isLast) return
            try {
                Discord.awaitStatus(Discord.Status.LOADING_SUBSYSTEMS, Discord.Status.RECONNECT_QUEUED)
            } catch (ex: IllegalStateException) {
                close()
            }
        }

        override fun hashCode(): Int {
            return Objects.hash("C", Discord)
        }

        override fun equals(other: Any?): Boolean {
            return if (other === this) true else other is StartingNode
        }
    }

    private class ReconnectNode: SessionConnectNode {
        @Throws(InterruptedException::class)
        override fun run(isLast: Boolean) {
            if (shutdown) return
            reconnect()
            if (isLast) return
            try {
                Discord.awaitStatus(Discord.Status.LOADING_SUBSYSTEMS, Discord.Status.RECONNECT_QUEUED)
            } catch (ex: IllegalStateException) {
                close()
            }
        }
    }
}