package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA

abstract class SocketHandler(protected val jda: JDA) {
    var responseNumber: Long = 0
    var allContent: DataObject? = null

    @Synchronized
    fun handle(responseTotal: Long, dataObject: DataObject) {
        allContent = dataObject
        responseNumber = responseTotal
        val guildId = handleInternally(dataObject.getObject("d"))
        if (guildId != null) jda.guildSetupController.cacheEvent(guildId, dataObject)
        allContent = null
    }

    protected abstract fun handleInternally(content: DataObject): Long?
}