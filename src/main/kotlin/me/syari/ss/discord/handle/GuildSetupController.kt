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

    private fun remove(id: Long) {
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
            setupNodes[id] = node
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

    private fun sendChunkRequest(any: Any) {
        val timeout = System.currentTimeMillis() + CHUNK_TIMEOUT
        synchronized(pendingChunks) {
            if (any is DataArray) {
                for (o in any) {
                    pendingChunks[o as Long] = timeout
                }
            } else {
                pendingChunks[any as Long] = timeout
            }
        }
        WebSocketClient.chunkOrSyncRequest(DataContainer().apply {
            put("op", MEMBER_CHUNK_REQUEST)
            put("d", DataContainer().apply {
                put("guild_id", any)
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
}