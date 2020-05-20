package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.entities.Guild.Companion.contains

class GuildCreateHandler(jda: JDA): SocketHandler(jda) {
    override fun handleInternally(content: DataObject): Long? {
        println(">> GuildCreateHandler")
        val id = content.getLong("id")
        if (!contains(id)) jda.guildSetupController.onCreate(id, content)
        return null
    }
}