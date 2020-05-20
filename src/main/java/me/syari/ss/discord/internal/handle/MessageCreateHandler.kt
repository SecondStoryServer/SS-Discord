package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.entities.EntityBuilder
import me.syari.ss.discord.internal.entities.TextChannel

class MessageCreateHandler(api: JDA): SocketHandler(api) {
    override fun handleInternally(content: DataObject): Long? {
        println(">> MessageCreateHandler")
        if (content.getInt("type") != 0) return null
        if (!content.isNull("guild_id")) {
            val guildId = content.getLong("guild_id")
            if (jda.guildSetupController.isLocked(guildId)) return guildId
        }
        val channelId = content.getLong("channel_id")
        val channel = TextChannel.get(channelId) ?: return null
        val message = try {
            jda.entityBuilder.createMessage(content, channel)
        } catch (ex: IllegalArgumentException) {
            return when (ex.message) {
                EntityBuilder.MISSING_CHANNEL -> {
                    jda.eventCache.cache(
                        EventCache.Type.CHANNEL, channelId, responseNumber, allContent!!
                    ) { responseTotal: Long, dataObject: DataObject ->
                        handle(responseTotal, dataObject)
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
        jda.callMessageReceiveEvent(message)
        return null
    }
}