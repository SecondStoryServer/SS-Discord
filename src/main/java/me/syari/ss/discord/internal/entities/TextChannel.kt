package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.WithId
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.requests.Requester
import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TextChannel(override val idLong: Long, val guild: Guild, val name: String): WithId, Comparable<TextChannel> {
    companion object {
        private const val MAX_CONTENT_LENGTH = 2000

        private val textChannelCache = mutableMapOf<Long, TextChannel?>()

        fun get(id: Long): TextChannel? {
            return textChannelCache.getOrPut(id) { getFromGuild(id) }
        }

        private fun getFromGuild(id: Long): TextChannel? {
            for (guild in Guild.allGuild) {
                return guild.getTextChannel(id) ?: continue
            }
            return null
        }
    }

    val api: JDA = guild.api

    val asMention: String
        get() = "<#$idLong>"

    fun sendMessage(text: String) {
        val length = text.length
        if (length == 0) return
        if (MAX_CONTENT_LENGTH < length) {
            sendMessage(text.substring(0, 2000))
            sendMessage(text.substring(2000))
            return
        }
        val route = Route.getSendMessageRoute(id)
        val messageAction = MessageAction(api, route, this, text)
        messageAction.queue()
    }

    override fun toString(): String {
        return "TextChannel:$name($idLong)"
    }

    override fun compareTo(other: TextChannel): Int {
        return java.lang.Long.compareUnsigned(idLong, other.idLong)
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is TextChannel) return false
        return other.idLong == idLong
    }

    private class MessageAction(
        val api: JDA, route: Route, private val channel: TextChannel, private val content: String
    ): RestAction<Message>(api, route) {
        override fun finalizeData(): RequestBody {
            return DataObject.empty().apply {
                put("content", content)
            }.toString().toRequestBody(Requester.MEDIA_TYPE_JSON)
        }

        override fun handleSuccess(response: Response, request: Request<Message>) {
            val message = api.entityBuilder.createMessage(response.dataObject, channel)
            request.onSuccess(message)
        }
    }
}