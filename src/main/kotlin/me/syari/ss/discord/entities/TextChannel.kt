package me.syari.ss.discord.entities

import me.syari.ss.discord.data.DataContainer
import me.syari.ss.discord.requests.Request
import me.syari.ss.discord.requests.Requester
import me.syari.ss.discord.requests.Response
import me.syari.ss.discord.requests.RestAction
import me.syari.ss.discord.requests.Route
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TextChannel(
    override val idLong: Long, val guild: Guild, val name: String
): WithId {
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
        val route = Route.sendMessageRoute(id)
        val messageAction = MessageAction(route, this, text)
        messageAction.queue()
    }

    override fun toString(): String {
        return "TextChannel:$name($idLong)"
    }

    private class MessageAction(
        route: Route, private val channel: TextChannel, private val content: String
    ): RestAction<Message>(route) {
        override fun finalizeData(): RequestBody {
            return DataContainer().apply {
                put("content", content)
            }.toString().toRequestBody(Requester.MEDIA_TYPE_JSON)
        }

        override fun handleSuccess(response: Response, request: Request<Message>) {
            val message = EntityBuilder.createMessage(response.dataObject, channel)
            request.onSuccess(message)
        }
    }
}