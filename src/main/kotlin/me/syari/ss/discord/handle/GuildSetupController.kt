package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.requests.WebSocketClient

internal object GuildSetupController {
    private val setupNodes = mutableMapOf<Long, GuildSetupNode>()

    private fun remove(id: Long) {
        setupNodes.remove(id)
    }

    fun ready(id: Long) {
        remove(id)
        if (!WebSocketClient.isReady) {
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
    }

}