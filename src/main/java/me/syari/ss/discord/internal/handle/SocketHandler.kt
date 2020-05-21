package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.internal.Discord

abstract class SocketHandler {
    var responseNumber: Long = 0
    var allContent: DataObject? = null

    @Synchronized
    fun handle(responseTotal: Long, dataObject: DataObject) {
        allContent = dataObject
        responseNumber = responseTotal
        val guildId = handleInternally(dataObject.getObject("d"))
        if (guildId != null) Discord.guildSetupController.cacheEvent(guildId, dataObject)
        allContent = null
    }

    abstract fun handleInternally(content: DataObject): Long?
}