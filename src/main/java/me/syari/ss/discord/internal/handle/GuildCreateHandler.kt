package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.internal.entities.Guild.Companion.contains

object GuildCreateHandler: SocketHandler() {
    override fun handleInternally(content: DataObject): Long? {
        println(">> GuildCreateHandler")
        val id = content.getLong("id")
        if (!contains(id)) GuildSetupController.onCreate(id, content)
        return null
    }
}