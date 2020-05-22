package me.syari.ss.discord.entities

import java.lang.Long.hashCode
import java.util.regex.Matcher
import java.util.regex.Pattern

class Message(
    private val id: Long, val channel: TextChannel, private val content: String, val author: User, val member: Member?
) {
    private val mutex = Any()
    val guild = channel.guild

    val contentDisplay by lazy {
        synchronized(mutex) {
            var contentDisplay = content
            for (user in mentionedUser) {
                val member = guild.getMember(user)
                val name = member?.displayName ?: user.name
                contentDisplay = contentDisplay.replace("<@!?${user.id}>", "@$name")
            }
            for (emote in emotes) {
                contentDisplay = contentDisplay.replace(emote.asMention, ":${emote.name}:")
            }
            for (mentionedChannel in mentionedChannels) {
                contentDisplay = contentDisplay.replace(mentionedChannel.asMention, "#${mentionedChannel.name}")
            }
            for (mentionedRole in mentionedRoles) {
                contentDisplay = contentDisplay.replace(mentionedRole.asMention, "@${mentionedRole.name}")
            }
            contentDisplay
        }
    }

    @get:Synchronized
    private val mentionedUser by lazy {
        processMentions(MentionType.USER) { matcher ->
            val userId = parseSnowflake(matcher.group(1))
            User.get(userId)
        }
    }

    @get:Synchronized
    private val mentionedChannels by lazy {
        processMentions(MentionType.CHANNEL) { matcher ->
            val channelId = parseSnowflake(matcher.group(1))
            TextChannel.get(channelId)
        }
    }

    @get:Synchronized
    private val mentionedRoles by lazy {
        processMentions(MentionType.ROLE) { matcher ->
            val roleId = parseSnowflake(matcher.group(1))
            guild.getRole(roleId)
        }
    }

    @get:Synchronized
    private val emotes by lazy {
        processMentions(MentionType.EMOTE) { matcher ->
            val emoteId = parseSnowflake(matcher.group(2))
            Emote.get(emoteId) {
                val name = matcher.group(1)
                val animated = matcher.group(0).startsWith("<a:")
                Emote(emoteId, name, animated)
            }
        }
    }

    private fun <T> processMentions(
        type: MentionType, run: (Matcher) -> T?
    ): Set<T> {
        val list = mutableSetOf<T>()
        val matcher = type.pattern.matcher(content)
        while (matcher.find()) {
            try {
                run.invoke(matcher)?.let {
                    list.add(it)
                }
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
            }
        }
        return list
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Message) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return hashCode(id)
    }

    override fun toString(): String {
        return String.format("Message:$author:%.20s($id)", this)
    }

    private enum class MentionType(regex: String) {
        USER("<@!?(\\d+)>"),
        ROLE("<@&(\\d+)>"),
        CHANNEL("<#(\\d+)>"),
        EMOTE("<a?:([a-zA-Z0-9_]+):([0-9]+)>");

        val pattern: Pattern = Pattern.compile(regex)
    }

    companion object {
        private fun parseSnowflake(input: String): Long {
            return try {
                input.toLong()
            } catch (ex: NumberFormatException) {
                throw NumberFormatException("The specified ID is not a valid snowflake ($input). Expecting a valid long value!")
            }
        }
    }

}