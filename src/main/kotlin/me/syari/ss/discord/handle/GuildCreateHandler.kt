package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.entities.Guild.Companion.contains

object GuildCreateHandler: SocketHandler() {
    override fun handleInternally(content: DataContainer): Long? {
        val id = content.getLongOrThrow("id")
        if (!contains(id)) GuildSetupController.onCreate(id, content)
        return null
    }
}