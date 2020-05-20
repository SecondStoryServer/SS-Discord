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
import me.syari.ss.discord.api.requests.CloseCode
import me.syari.ss.discord.api.requests.CloseCode.Companion.from
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.handle.EventCache
import me.syari.ss.discord.internal.handle.GuildCreateHandler
import me.syari.ss.discord.internal.handle.MessageCreateHandler
import me.syari.ss.discord.internal.utils.ZlibDecompressor
import java.io.IOException
import java.net.URI
import java.util.Objects
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.zip.DataFormatException

class WebSocketClient(val jDA: JDA): WebSocketAdapter(), WebSocketListener {
    var socket: WebSocket? = null
    private var sessionId: String? = null
    private val readLock = Any()
    private val decompressor = ZlibDecompressor()
    val queueLock = ReentrantLock()
    val executor: ScheduledExecutorService
    private var ratelimitThread: WebSocketSendingThread? = null

    @Volatile
    private var keepAliveThread: Future<*>? = null
    private var initiating = false
    private var reconnectTimeoutS = 2
    private var identifyTime: Long = 0
    val chunkSyncQueue: Queue<String> = ConcurrentLinkedQueue()
    val ratelimitQueue: Queue<String> = ConcurrentLinkedQueue()

    @Volatile
    private var ratelimitResetTime: Long = 0
    private val messagesSent = AtomicInteger(0)

    @Volatile
    private var shutdown = false
    private var shouldReconnect: Boolean
    private var handleIdentifyRateLimit = false
    private var connected = false

    @Volatile
    var sentAuthInfo = false
    private var processingReady = true

    @Volatile
    private var connectNode: SessionConnectNode?

    fun ready() {
        if (initiating) {
            initiating = false
            processingReady = false
        }
        jDA.status = JDA.Status.CONNECTED
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
            socket!!.sendText(message)
            messagesSent.getAndIncrement()
            true
        } else {
            false
        }
    }

    private fun setupSendingThread() {
        ratelimitThread = WebSocketSendingThread(this)
        ratelimitThread!!.start()
    }

    fun close() {
        if (socket != null) socket!!.sendClose(1000)
    }

    fun close(code: Int, reason: String?) {
        if (socket != null) socket!!.sendClose(code, reason)
    }

    @Synchronized
    fun shutdown() {
        shutdown = true
        shouldReconnect = false
        if (connectNode != null) jDA.sessionController.removeSession(connectNode!!)
        close(1000, "Shutting down")
    }

    @Synchronized
    private fun connect() {
        if (jDA.status !== JDA.Status.ATTEMPTING_TO_RECONNECT) jDA.status = JDA.Status.CONNECTING_TO_WEBSOCKET
        if (shutdown) throw RejectedExecutionException("JDA is shutdown!")
        initiating = true
        val url = jDA.gatewayUrl + "?encoding=json&v=" + DISCORD_GATEWAY_VERSION + "&compress=zlib-stream"
        try {
            val socketFactory = jDA.webSocketFactory
            synchronized(socketFactory) {
                val host = URI.create(url).host
                if (host != null) {
                    socketFactory.setServerName(host)
                } else {
                    socketFactory.serverNames = null
                }
                socket = socketFactory.createSocket(url)
            }
            socket!!.addHeader("Accept-Encoding", "gzip").addListener(this).connect()
        } catch (ex: IOException) {
            jDA.resetGatewayUrl()
            throw IllegalStateException(ex)
        } catch (ex: WebSocketException) {
            jDA.resetGatewayUrl()
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
        jDA.status = JDA.Status.IDENTIFYING_SESSION
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
        jDA.status = JDA.Status.DISCONNECTED
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
            if (ratelimitThread != null) {
                ratelimitThread!!.shutdown()
                ratelimitThread = null
            }
            decompressor.reset()
            jDA.shutdownInternals()
        } else {
            synchronized(readLock) { decompressor.reset() }
            if (isInvalidate) invalidate()
            try {
                handleReconnect()
            } catch (e: InterruptedException) {
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
            jDA.status = JDA.Status.RECONNECT_QUEUED
            connectNode = ReconnectNode().apply {
                jDA.sessionController.appendSession(this)
            }
        } catch (ex: IllegalStateException) {
            jDA.status = JDA.Status.SHUTDOWN
        }
    }

    @Throws(InterruptedException::class)
    private fun reconnect() {
        if (shutdown) {
            jDA.status = JDA.Status.SHUTDOWN
            return
        }
        while (shouldReconnect) {
            jDA.status = JDA.Status.WAITING_TO_RECONNECT
            val delay = reconnectTimeoutS
            reconnectTimeoutS = Math.min(reconnectTimeoutS shl 1, 900)
            Thread.sleep(delay * 1000.toLong())
            handleIdentifyRateLimit = false
            jDA.status = JDA.Status.ATTEMPTING_TO_RECONNECT
            try {
                connect()
                break
            } catch (ex: RejectedExecutionException) {
                jDA.status = JDA.Status.SHUTDOWN
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
            DataObject.empty().put("op", WebSocketCode.HEARTBEAT).put("d", jDA.responseTotal).toString()
        send(keepAlivePacket, true)
    }

    private fun sendIdentify() {
        val connectionProperties =
            DataObject.empty().put("\$os", System.getProperty("os.name")).put("\$browser", "JDA").put("\$device", "JDA")
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
        jDA.status = JDA.Status.AWAITING_LOGIN_CONFIRMATION
    }

    private fun sendResume() {
        val resume = DataObject.empty().put("op", WebSocketCode.RESUME).put(
            "d", DataObject.empty().put("session_id", sessionId).put("token", token).put("seq", jDA.responseTotal)
        )
        send(resume.toString(), true)
        jDA.status = JDA.Status.AWAITING_LOGIN_CONFIRMATION
    }

    private fun invalidate() {
        sessionId = null
        sentAuthInfo = false
        locked(Runnable { chunkSyncQueue.clear() })
        jDA.eventCache.clear()
        jDA.guildSetupController.clearCache()
    }

    private val token: String
        private get() = jDA.token.substring("Bot ".length)

    private fun handleEvent(content: DataObject) {
        try {
            onEvent(content)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun onEvent(content: DataObject) {
        val opCode = content.getInt("op")
        if (!content.isNull("s")) jDA.setResponseTotal(content.getInt("s"))
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
        val responseTotal = jDA.responseTotal
        if (raw["d"] !is Map<*, *>) {
            return
        }
        val content = raw.getObject("d")
        val jda = jDA
        try {
            when (type) {
                "READY" -> {
                    reconnectTimeoutS = 2
                    jDA.status = JDA.Status.LOADING_SUBSYSTEMS
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
                        jda.status = JDA.Status.LOADING_SUBSYSTEMS
                    }
                }
                "GUILD_CREATE" -> GuildCreateHandler(jDA).handle(responseTotal, raw)
                "MESSAGE_CREATE" -> MessageCreateHandler(jDA).handle(responseTotal, raw)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        if (responseTotal % EventCache.TIMEOUT_AMOUNT == 0L) jda.eventCache.timeout(responseTotal)
    }

    @Throws(DataFormatException::class)
    override fun onBinaryMessage(
        websocket: WebSocket, binary: ByteArray
    ) {
        var json: DataObject?
        synchronized(readLock) { json = handleBinary(binary) }
        if (json != null) handleEvent(json!!)
    }

    @Throws(DataFormatException::class)
    private fun handleBinary(binary: ByteArray): DataObject? {
        val json: String?
        try {
            json = decompressor.decompress(binary)
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

    private inner class StartingNode: SessionConnectNode {
        @Throws(InterruptedException::class)
        override fun run(isLast: Boolean) {
            if (shutdown) return
            setupSendingThread()
            connect()
            if (isLast) return
            try {
                jDA.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED)
            } catch (ex: IllegalStateException) {
                close()
            }
        }

        override fun hashCode(): Int {
            return Objects.hash("C", jDA)
        }

        override fun equals(other: Any?): Boolean {
            return if (other === this) true else other is StartingNode
        }
    }

    private inner class ReconnectNode: SessionConnectNode {
        @Throws(InterruptedException::class)
        override fun run(isLast: Boolean) {
            if (shutdown) return
            reconnect()
            if (isLast) return
            try {
                jDA.awaitStatus(JDA.Status.LOADING_SUBSYSTEMS, JDA.Status.RECONNECT_QUEUED)
            } catch (ex: IllegalStateException) {
                close()
            }
        }

        override fun hashCode(): Int {
            return Objects.hash("R", jDA)
        }

        override fun equals(`object`: Any?): Boolean {
            return if (`object` === this) true else `object` is ReconnectNode
        }
    }

    companion object {
        private const val DISCORD_GATEWAY_VERSION = 6
        private const val INVALIDATE_REASON = "INVALIDATE_SESSION"
        private val IDENTIFY_BACKOFF = TimeUnit.SECONDS.toMillis(SessionController.IDENTIFY_DELAY.toLong())
    }

    init {
        executor = jDA.gatewayPool
        shouldReconnect = true
        connectNode = StartingNode().apply {
            try {
                jDA.sessionController.appendSession(this)
            } catch (e: RuntimeException) {
                jDA.status = JDA.Status.SHUTDOWN
                throw e
            } catch (e: Error) {
                jDA.status = JDA.Status.SHUTDOWN
                if (e is RuntimeException) {
                    throw (e as RuntimeException)
                } else {
                    throw e
                }
            }
        }
    }
}