package me.syari.ss.discord.internal.handle

import gnu.trove.map.hash.TLongObjectHashMap
import gnu.trove.set.TLongSet
import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.api.data.DataArray
import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.internal.Discord
import java.util.LinkedList

class GuildSetupNode internal constructor(private val id: Long, private val controller: GuildSetupController) {
    private val cachedEvents: MutableList<DataObject> = LinkedList()
    private var members = TLongObjectHashMap<DataObject>()
    private var removedMembers: TLongSet? = null
    private var partialGuild: DataObject? = null
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

    fun handleCreate(dataObject: DataObject) {
        val notNulPartialGuild = partialGuild?.apply {
            for (key in dataObject.keys()) {
                put(key, dataObject.opt(key).orElse(null))
            }
        } ?: {
            partialGuild = dataObject
            dataObject
        }.invoke()
        val unavailable = notNulPartialGuild.getBoolean("unavailable", false)
        if (unavailable) return
        expectedMemberCount = notNulPartialGuild.getInt("member_count")
        members = TLongObjectHashMap(expectedMemberCount)
        removedMembers = TLongHashSet()
        val memberArray = notNulPartialGuild.getArray("members")
        if (memberArray.length() < expectedMemberCount && !requestedChunk) {
            updateStatus(GuildSetupController.Status.CHUNKING)
            controller.addGuildForChunking(id)
            requestedChunk = true
        } else if (handleMemberChunk(memberArray) && !requestedChunk) {
            members.clear()
            updateStatus(GuildSetupController.Status.CHUNKING)
            controller.addGuildForChunking(id)
            requestedChunk = true
        }
    }

    private fun handleMemberChunk(arr: DataArray): Boolean {
        if (partialGuild == null) return true
        for (index in 0 until arr.length()) {
            val obj = arr.getObject(index)
            val id = obj.getObject("user").getLong("id")
            members.put(id, obj)
        }
        if (expectedMemberCount <= members.size()) {
            completeSetup()
            return false
        }
        return true
    }

    fun cacheEvent(event: DataObject) {
        cachedEvents.add(event)
        val cacheSize = cachedEvents.size
        if (2000 <= cacheSize && cacheSize % 1000 == 0 && status === GuildSetupController.Status.CHUNKING) {
            controller.sendChunkRequest(id)
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
        partialGuild?.let { Discord.entityBuilder.createGuild(id, it) }
        if (requestedChunk) {
            controller.ready(id)
        } else {
            controller.remove(id)
        }
        updateStatus(GuildSetupController.Status.READY)
        Discord.client.handle(cachedEvents)
        Discord.eventCache.playbackCache(EventCache.Type.GUILD, id)
    }
}