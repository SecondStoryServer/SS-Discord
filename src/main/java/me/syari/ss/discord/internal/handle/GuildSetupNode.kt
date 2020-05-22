package me.syari.ss.discord.internal.handle

import gnu.trove.map.hash.TLongObjectHashMap
import gnu.trove.set.TLongSet
import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.api.data.DataArray
import me.syari.ss.discord.api.data.DataContainer
import me.syari.ss.discord.internal.entities.EntityBuilder
import me.syari.ss.discord.internal.requests.WebSocketClient
import java.util.LinkedList

class GuildSetupNode(private val id: Long) {
    private val cachedEvents: MutableList<DataContainer> = LinkedList()
    private var members = TLongObjectHashMap<DataContainer>()
    private var removedMembers: TLongSet? = null
    private var partialGuild: DataContainer? = null
    private var expectedMemberCount = 1
    private var requestedChunk = false
    private var status = GuildSetupController.Status.INIT

    override fun toString(): String {
        return "GuildSetupNode[$id|$status]{expectedMemberCount=$expectedMemberCount, requestedChunk=$requestedChunk}"
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(id)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GuildSetupNode) return false
        return other.id == id
    }

    private fun updateStatus(status: GuildSetupController.Status) {
        if (status !== this.status) this.status = status
    }

    fun handleCreate(dataObject: DataContainer) {
        val notNulPartialGuild = partialGuild?.apply {
            for (key in dataObject.keys) {
                put(key, dataObject.get(key))
            }
        } ?: {
            partialGuild = dataObject
            dataObject
        }.invoke()
        val unavailable = notNulPartialGuild.getBoolean("unavailable") ?: false
        if (unavailable) return
        expectedMemberCount = notNulPartialGuild.getIntOrThrow("member_count")
        members = TLongObjectHashMap(expectedMemberCount)
        removedMembers = TLongHashSet()
        val memberArray = notNulPartialGuild.getArrayOrThrow("members")
        if (memberArray.size < expectedMemberCount && !requestedChunk) {
            updateStatus(GuildSetupController.Status.CHUNKING)
            GuildSetupController.addGuildForChunking(id)
            requestedChunk = true
        } else if (handleMemberChunk(memberArray) && !requestedChunk) {
            members.clear()
            updateStatus(GuildSetupController.Status.CHUNKING)
            GuildSetupController.addGuildForChunking(id)
            requestedChunk = true
        }
    }

    private fun handleMemberChunk(arr: DataArray): Boolean {
        if (partialGuild == null) return true
        for (index in 0 until arr.size) {
            val obj = arr.getContainerOrThrow(index)
            val id = obj.getContainerOrThrow("user").getLongOrThrow("id")
            members.put(id, obj)
        }
        if (expectedMemberCount <= members.size()) {
            completeSetup()
            return false
        }
        return true
    }

    fun cacheEvent(event: DataContainer) {
        cachedEvents.add(event)
        val cacheSize = cachedEvents.size
        if (2000 <= cacheSize && cacheSize % 1000 == 0 && status === GuildSetupController.Status.CHUNKING) {
            GuildSetupController.sendChunkRequest(id)
        }
    }

    private fun completeSetup() {
        updateStatus(GuildSetupController.Status.BUILDING)
        removedMembers?.let { removedMembers ->
            val iterator = removedMembers.iterator()
            while (iterator.hasNext()) {
                members.remove(iterator.next())
            }
            removedMembers.clear()
        }
        partialGuild?.let { EntityBuilder.createGuild(id, it) }
        if (requestedChunk) {
            GuildSetupController.ready(id)
        } else {
            GuildSetupController.remove(id)
        }
        updateStatus(GuildSetupController.Status.READY)
        WebSocketClient.handle(cachedEvents)
        EventCache.playbackCache(EventCache.Type.GUILD, id)
    }
}