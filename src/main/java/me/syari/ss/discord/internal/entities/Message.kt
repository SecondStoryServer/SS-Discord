package me.syari.ss.discord.internal.entities

import gnu.trove.set.TLongSet
import me.syari.ss.discord.internal.JDA
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

class Message(
    private val id: Long,
    val channel: TextChannel,
    private val mentionedUsers: TLongSet,
    private val mentionedRoles: TLongSet,
    private val contentRaw: String,
    val author: User,
    val member: Member?
) {
    private val mutex = Any()
    val api: JDA = channel.api
    private var altContent: String? = null
    private var userMentions: List<User?>? = null
    private var emoteMentions: List<Emote>? = null
    private var roleMentions: List<Role>? = null
    private var channelMentions: List<TextChannel>? = null

    val contentDisplay: String
        get() {
            altContent?.let { return it }
            synchronized(mutex) {
                altContent?.let { return it }
                var tmp = contentRaw
                for (user in getMentionedUsers().filterNotNull()) {
                    val member = guild.getMember(user)
                    val name = member?.displayName ?: user.name
                    tmp = tmp.replace("<@!?${Pattern.quote(user.id)}>", "@${Matcher.quoteReplacement(name)}")
                }
                for (emote in emotes) {
                    tmp = tmp.replace(emote.asMention, ":${emote.name}:")
                }
                for (mentionedChannel in mentionedChannels) {
                    tmp = tmp.replace(mentionedChannel.asMention, "#${mentionedChannel.name}")
                }
                for (mentionedRole in getMentionedRoles()) {
                    tmp = tmp.replace(mentionedRole.asMention, "@${mentionedRole.name}")
                }
                return tmp.also { altContent = it }
            }
        }

    val guild: Guild
        get() = channel.guild

    @Synchronized
    private fun getMentionedUsers(): List<User?> {
        if (userMentions == null) userMentions =
            Collections.unmodifiableList(processMentions(MentionType.USER, ArrayList(), Function { matcher: Matcher ->
                matchUser(matcher)
            }))
        return userMentions!!
    }

    private fun matchUser(matcher: Matcher): User? {
        val userId = parseSnowflake(matcher.group(1))
        if (!mentionedUsers.contains(userId)) return null
        var user = api.getUserById(userId)
        if (user == null) user = api.fakeUserMap[userId]
        if (user == null && userMentions != null) user =
            userMentions!!.stream().filter { it: User? -> it!!.idLong == userId }.findFirst().orElse(null)
        return user
    }

    @get:Synchronized
    private val mentionedChannels: List<TextChannel>
        get() {
            if (channelMentions == null) channelMentions =
                Collections.unmodifiableList(processMentions(MentionType.CHANNEL,
                    ArrayList(),
                    Function { matcher: Matcher ->
                        matchTextChannel(matcher)
                    })).filterNotNull()
            return channelMentions!!
        }

    private fun matchTextChannel(matcher: Matcher): TextChannel? {
        val channelId = parseSnowflake(matcher.group(1))
        return api.getTextChannelById(channelId)
    }

    @Synchronized
    private fun getMentionedRoles(): List<Role> {
        if (roleMentions == null) roleMentions =
            Collections.unmodifiableList(processMentions(MentionType.ROLE, ArrayList(), Function { matcher: Matcher ->
                matchRole(matcher)
            })).filterNotNull()
        return roleMentions!!
    }

    private fun matchRole(matcher: Matcher): Role? {
        val roleId = parseSnowflake(matcher.group(1))
        return if (!mentionedRoles.contains(roleId)) null else guild.getRoleById(roleId)
    }

    @get:Synchronized
    private val emotes: List<Emote>
        get() {
            if (emoteMentions == null) emoteMentions =
                Collections.unmodifiableList(processMentions(MentionType.EMOTE, ArrayList(), Function { m: Matcher ->
                    matchEmote(m)
                }))
            return emoteMentions!!
        }

    private fun matchEmote(m: Matcher): Emote {
        val emoteId = parseSnowflake(m.group(2))
        val name = m.group(1)
        val animated = m.group(0).startsWith("<a:")
        var emote = api.getEmoteById(emoteId)
        if (emote == null) {
            emote = Emote(emoteId)
            emote.name = name
            emote.isAnimated = animated
        }
        return emote
    }

    fun setMentions(
        users: MutableList<User>, members: MutableList<Member>
    ) {
        users.sortWith(Comparator.comparing { user: User ->
            contentRaw.indexOf("<@" + user.id + ">").coerceAtLeast(contentRaw.indexOf("<@!" + user.id + ">"))
        })
        members.sortWith(Comparator.comparing { user: Member ->
            contentRaw.indexOf("<@" + user.id + ">").coerceAtLeast(contentRaw.indexOf("<@!" + user.id + ">"))
        })
        userMentions = Collections.unmodifiableList(users)
    }

    private fun <T, C: MutableCollection<T>> processMentions(
        type: MentionType, collection: C, map: Function<Matcher, T>
    ): C {
        val matcher = type.pattern.matcher(contentRaw)
        while (matcher.find()) {
            try {
                val elem: T? = map.apply(matcher)
                if (elem == null || collection.contains(elem)) continue
                collection.add(elem)
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
            }
        }
        return collection
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Message) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(id)
    }

    override fun toString(): String {
        return String.format("M:%s:%.20s(%s)", author, this, id)
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
                if (input.startsWith("-")) {
                    input.toLong()
                } else {
                    java.lang.Long.parseUnsignedLong(input)
                }
            } catch (ex: NumberFormatException) {
                throw NumberFormatException(
                    String.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input)
                )
            }
        }
    }

}