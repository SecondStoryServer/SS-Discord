package me.syari.ss.discord.internal.handle

import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.entities.EntityBuilder

class MessageCreateHandler(api: JDA): SocketHandler(api) {
    override fun handleInternally(content: DataObject): Long? {
        println(">> MessageCreateHandler")
        if (content.getInt("type") != 0) return null
        if (!content.isNull("guild_id")) {
            val guildId = content.getLong("guild_id")
            if (api.guildSetupController.isLocked(guildId)) return guildId
        }
        val message = try {
            api.entityBuilder.createMessage(content)
        } catch (ex: IllegalArgumentException) {
            return when (ex.message) {
                EntityBuilder.MISSING_CHANNEL -> {
                    val channelId = content.getLong("channel_id")
                    api.eventCache.cache(
                        EventCache.Type.CHANNEL,
                        channelId,
                        responseNumber,
                        allContent
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
        api.callMessageReceiveEvent(message)
        return null
    }
}