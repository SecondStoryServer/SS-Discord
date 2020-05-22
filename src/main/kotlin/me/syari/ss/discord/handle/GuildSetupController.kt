package me.syari.ss.discord.handle

import gnu.trove.map.TLongLongMap
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongLongHashMap
import gnu.trove.map.hash.TLongObjectHashMap
import gnu.trove.set.TLongSet
import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.data.DataArray
import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.requests.WebSocketClient
import me.syari.ss.discord.requests.WebSocketCode

object GuildSetupController {
    private const val CHUNK_TIMEOUT = 10000
    private val setupNodes: TLongObjectMap<GuildSetupNode> = TLongObjectHashMap()
    private val chunkingGuilds: TLongSet = TLongHashSet()
    private val pendingChunks: TLongLongMap = TLongLongHashMap()
    private val unavailableGuilds: TLongSet = TLongHashSet()
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

    fun isLocked(id: Long): Boolean {
        return setupNodes.containsKey(id)
    }

    fun cacheEvent(guildId: Long, event: DataContainer?) {
        val node = setupNodes[guildId]
        if (event != null) {
            node.cacheEvent(event)
        }
    }

    fun clearCache() {
        setupNodes.clear()
        chunkingGuilds.clear()
        unavailableGuilds.clear()
        incompleteCount = 0
        synchronized(pendingChunks) { pendingChunks.clear() }
    }

    fun sendChunkRequest(`object`: Any) {
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
            put("op", WebSocketCode.MEMBER_CHUNK_REQUEST)
            put("d", DataContainer().apply {
                put("guild_id", `object`)
                put("query", "")
                put("limit", 0)
            })
        })
    }

    private fun tryChunking() {
        if (chunkingGuilds.size() >= 50) {
            val subset = DataArray()
            val it = chunkingGuilds.iterator()
            while (subset.size < 50) {
                subset.add(it.next())
                it.remove()
            }
            sendChunkRequest(subset)
        }
        if (incompleteCount > 0 && chunkingGuilds.size() >= incompleteCount) {
            val array = DataArray()
            chunkingGuilds.forEach { guild: Long ->
                array.add(guild)
                true
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