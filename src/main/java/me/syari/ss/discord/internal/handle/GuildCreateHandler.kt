package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.data.DataContainer
import me.syari.ss.discord.internal.entities.Guild.Companion.contains

object GuildCreateHandler: SocketHandler() {
    override fun handleInternally(content: DataContainer): Long? {
        println(">> GuildCreateHandler")
        val id = content.getLongOrThrow("id")
        if (!contains(id)) GuildSetupController.onCreate(id, content)
        return null
    }
}