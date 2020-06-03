package me.syari.ss.discord.entities

import java.util.regex.Matcher
import java.util.regex.Pattern

class Message(
    val channel: TextChannel, val content: String, val authorUser: User, val authorMember: Member?
) {
    private val mutex = Any()

    val contentDisplay by lazy {
        synchronized(mutex) {
            var contentDisplay = content
            for (user in mentionedUser) {
                val member = channel.guild.getMember(user.idLong)
                val name = member?.displayName ?: user.name
                contentDisplay = contentDisplay.replace("<@!?${user.id}>".toRegex(), "@$name")
            }
            for (mention in emotes + mentionedChannels + mentionedRoles) {
                contentDisplay = (mention as Mentionable).replaceMentionAsDisplay(contentDisplay)
            }
            for (replace in replaceMap) {
                contentDisplay = contentDisplay.replace(replace.key, replace.value)
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
            channel.guild.getRole(roleId)
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

        private val replaceMap = mapOf(
            "@here" to "\\@here", "@everyone" to "\\@everyone"
        )
    }

}