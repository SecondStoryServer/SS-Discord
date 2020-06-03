package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataArray
import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.requests.WebSocketClient

internal object GuildSetupController {
    private const val CHUNK_TIMEOUT = 10000
    private const val MEMBER_CHUNK_REQUEST = 8
    private val setupNodes = mutableMapOf<Long, GuildSetupNode>()
    private val chunkingGuilds = mutableSetOf<Long>()
    private val pendingChunks = mutableMapOf<Long, Long>()
    private val unavailableGuilds = mutableSetOf<Long>()
    private var incompleteCount = 0

    fun addGuildForChunking(id: Long) {
        if (incompleteCount <= 0) {
            sendChunkRequest(id)
        } else {
            incompleteCount++
            chunkingGuilds.add(id)
            tryChunking()
        }
    }

    fun remove(id: Long) {
        unavailableGuilds.remove(id)
        setupNodes.remove(id)
        chunkingGuilds.remove(id)
        synchronized(pendingChunks) { pendingChunks.remove(id) }
    }

    fun ready(id: Long) {
        remove(id)
        incompleteCount--
        if (incompleteCount < 1 && !WebSocketClient.isReady) {
            WebSocketClient.ready()
        } else {
            tryChunking()
        }
    }

    fun onCreate(id: Long, obj: DataContainer) {
        var node = setupNodes[id]
        if (node == null) {
            node = GuildSetupNode(id)
            setupNodes.put(id, node)
        }
        node.handleCreate(obj)
    }

    fun clearCache() {
        setupNodes.clear()
        chunkingGuilds.clear()
        unavailableGuilds.clear()
        incompleteCount = 0
        synchronized(pendingChunks) { pendingChunks.clear() }
    }

    private fun sendChunkRequest(`object`: Any) {
        val timeout = System.currentTimeMillis() + CHUNK_TIMEOUT
        synchronized(pendingChunks) {
            if (`object` is DataArray) {
                for (o in `object`) {
                    pendingChunks.put(o as Long, timeout)
                }
            } else {
                pendingChunks.put(`object` as Long, timeout)
            }
        }
        WebSocketClient.chunkOrSyncRequest(DataContainer().apply {
            put("op", MEMBER_CHUNK_REQUEST)
            put("d", DataContainer().apply {
                put("guild_id", `object`)
                put("query", "")
                put("limit", 0)
            })
        })
    }

    private fun tryChunking() {
        if (50 <= chunkingGuilds.size) {
            val subset = DataArray()
            val it = chunkingGuilds.iterator()
            while (subset.size < 50) {
                subset.add(it.next())
                it.remove()
            }
            sendChunkRequest(subset)
        }
        if (incompleteCount in 1..chunkingGuilds.size) {
            val array = DataArray()
            chunkingGuilds.forEach { guild: Long ->
                array.add(guild)
            }
            chunkingGuilds.clear()
            sendChunkRequest(array)
        }
    }

    enum class Status {
        INIT,
        CHUNKING,
        BUILDING,
        READY
    }
}