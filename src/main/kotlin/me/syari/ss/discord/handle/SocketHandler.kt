package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer

internal abstract class SocketHandler {
    @Synchronized
    fun handle(dataObject: DataContainer) {
        handleInternally(dataObject.getContainerOrThrow("d"))
    }

    abstract fun handleInternally(content: DataContainer)
}