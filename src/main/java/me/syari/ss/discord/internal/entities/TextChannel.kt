package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.api.requests.Response
import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.requests.Requester
import me.syari.ss.discord.internal.requests.RestAction
import me.syari.ss.discord.internal.requests.Route
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference
import okhttp3.RequestBody
import java.io.IOException
import java.io.InputStream
import java.util.HashSet
import java.util.function.LongFunction

class TextChannel(override val idLong: Long, val guild: Guild, val name: String): ISnowflake, Comparable<TextChannel> {
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
        api: JDA, route: Route?, private val channel: TextChannel, private val content: String
    ): RestAction<Message>(api, route) {
        private val ownedResources: MutableSet<InputStream> = HashSet()
        private fun clearResources() {
            for (ownedResource in ownedResources) {
                try {
                    ownedResource.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
            ownedResources.clear()
        }

        private fun asJSON(): RequestBody {
            val json = DataObject.empty()
            json.put("content", content)
            return RequestBody.create(Requester.MEDIA_TYPE_JSON, json.toString())
        }

        override fun finalizeData(): RequestBody {
            return asJSON()
        }

        override fun handleSuccess(
            response: Response, request: Request<Message>
        ) {
            request.onSuccess(api.entityBuilder.createMessage(response.dataObject, channel))
        }

        protected fun finalize() {
            if (ownedResources.isEmpty()) return
            clearResources()
        }

    }

    companion object {
        private const val MAX_CONTENT_LENGTH = 2000
    }
}