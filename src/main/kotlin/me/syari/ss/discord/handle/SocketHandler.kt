package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer

abstract class SocketHandler {
    var responseNumber: Long = 0
    var allContent: DataContainer? = null

    @Synchronized
    fun handle(responseTotal: Long, dataObject: DataContainer) {
        allContent = dataObject
        responseNumber = responseTotal
        val guildId = handleInternally(dataObject.getContainerOrThrow("d"))
        if (guildId != null) GuildSetupController.cacheEvent(guildId, dataObject)
        allContent = null
    }

    abstract fun handleInternally(content: DataContainer): Long?
}