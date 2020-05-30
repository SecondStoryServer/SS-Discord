package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer

internal abstract class SocketHandler {
    @Synchronized
    fun handle(dataObject: DataContainer) {
        val guildId = handleInternally(dataObject.getContainerOrThrow("d"))
        if (guildId != null) GuildSetupController.cacheEvent(guildId, dataObject)
    }

    abstract fun handleInternally(content: DataContainer): Long?
}