package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.data.DataObject

abstract class SocketHandler {
    var responseNumber: Long = 0
    var allContent: DataObject? = null

    @Synchronized
    fun handle(responseTotal: Long, dataObject: DataObject) {
        allContent = dataObject
        responseNumber = responseTotal
        val guildId = handleInternally(dataObject.getObject("d"))
        if (guildId != null) GuildSetupController.cacheEvent(guildId, dataObject)
        allContent = null
    }

    abstract fun handleInternally(content: DataObject): Long?
}