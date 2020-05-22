package me.syari.ss.discord.handle

import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.Discord
import me.syari.ss.discord.entities.EntityBuilder
import me.syari.ss.discord.entities.TextChannel

object MessageCreateHandler: SocketHandler() {
    override fun handleInternally(content: DataContainer): Long? {
        if (content.getIntOrThrow("type") != 0) return null
        val guildId = content.getLong("guild_id")
        if (guildId != null && GuildSetupController.isLocked(guildId)) {
            return guildId
        }
        val channelId = content.getLongOrThrow("channel_id")
        val channel = TextChannel.get(channelId) ?: return null
        val message = try {
            EntityBuilder.createMessage(content, channel)
        } catch (ex: IllegalArgumentException) {
            return when (ex.message) {
                EntityBuilder.MISSING_CHANNEL -> {
                    allContent?.let { allContent ->
                        EventCache.cache(
                            EventCache.Type.CHANNEL, channelId, responseNumber, allContent
                        ) { responseTotal, dataObject ->
                            handle(responseTotal, dataObject)
                        }
                    }
                    null
                }
                EntityBuilder.UNKNOWN_MESSAGE_TYPE -> {
                    null
                }
                else -> {
                    throw ex
                }
            }
        }
        Discord.callMessageReceiveEvent(message)
        return null
    }
}