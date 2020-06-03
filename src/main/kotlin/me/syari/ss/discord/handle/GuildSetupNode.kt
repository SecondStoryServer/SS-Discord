package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataArray
import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.entities.EntityBuilder
import me.syari.ss.discord.requests.WebSocketClient

internal class GuildSetupNode(private val id: Long) {
    private val cachedEvents = mutableListOf<DataContainer>()
    private var members = mutableMapOf<Long, DataContainer>()
    private val removedMembers = mutableSetOf<Long>()
    private var partialGuild: DataContainer? = null
    private var expectedMemberCount = 1
    private var requestedChunk = true
    private var status = GuildSetupController.Status.INIT

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
        removedMembers.clear()
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
            members[id] = obj
        }
        if (expectedMemberCount <= members.size) {
            completeSetup()
            return false
        }
        return true
    }

    private fun completeSetup() {
        updateStatus(GuildSetupController.Status.BUILDING)
        val iterator = removedMembers.iterator()
        while (iterator.hasNext()) {
            members.remove(iterator.next())
        }
        removedMembers.clear()
        partialGuild?.let { EntityBuilder.createGuild(id, it) }
        GuildSetupController.ready(id)
        updateStatus(GuildSetupController.Status.READY)
        WebSocketClient.handle(cachedEvents)
    }
}