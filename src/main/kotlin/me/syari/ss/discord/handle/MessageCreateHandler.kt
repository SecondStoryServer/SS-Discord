package me.syari.ss.discord.handle

import me.syari.ss.discord.Discord
import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.entities.EntityBuilder
import me.syari.ss.discord.entities.TextChannel

internal object MessageCreateHandler: SocketHandler() {
    override fun handleInternally(content: DataContainer) {
        if (content.getIntOrThrow("type") != 0) return
        val channelId = content.getLongOrThrow("channel_id")
        val channel = TextChannel.get(channelId) ?: return
        val message = try {
            EntityBuilder.createMessage(content, channel)
        } catch (ex: IllegalArgumentException) {
            when (ex.message) {
                EntityBuilder.MISSING_CHANNEL, EntityBuilder.UNKNOWN_MESSAGE_TYPE -> {
                    return
                }
                else -> {
                    throw ex
                }
            }
        }
        Discord.callMessageReceiveEvent(message)
        return
    }
}