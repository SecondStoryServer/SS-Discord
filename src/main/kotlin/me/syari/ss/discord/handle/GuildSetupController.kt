package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataArray
import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.requests.WebSocketClient

internal object GuildSetupController {
    private const val CHUNK_TIMEOUT = 10000
    private const val MEMBER_CHUNK_REQUEST = 8
    private val setupNodes = mutableMapOf<Long, GuildSetupNode>()
    private val pendingChunks = mutableMapOf<Long, Long>()
    private val unavailableGuilds = mutableSetOf<Long>()
    private var incompleteCount = 0

    private fun remove(id: Long) {
        unavailableGuilds.remove(id)
        setupNodes.remove(id)
        synchronized(pendingChunks) { pendingChunks.remove(id) }
    }

    fun ready(id: Long) {
        remove(id)
        incompleteCount--
        if (incompleteCount < 1 && !WebSocketClient.isReady) {
            WebSocketClient.ready()
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
        unavailableGuilds.clear()
        incompleteCount = 0
        synchronized(pendingChunks) { pendingChunks.clear() }
    }

}