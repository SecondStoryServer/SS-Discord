package me.syari.ss.discord.internal.entities

import gnu.trove.set.TLongSet
import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.api.utils.data.DataArray
import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.handle.EventCache
import me.syari.ss.discord.internal.utils.Check
import java.util.ArrayList
import java.util.function.Function

class EntityBuilder(private val api: JDA) {
    private val guildCache = mutableMapOf<Long, Guild>()
    private val userCache = mutableMapOf<Long, User>()

    fun createGuild(id: Long, data: DataObject): Guild {
        val name = data.getString("name", "")
        val roleData = data.getArray("roles")
        val guild = Guild(api, id, name)
        Guild.add(guild)
        guildCache[id] = guild
        val roles = mutableMapOf<Long, Role>()
        for(i in 0 until roleData.length()){
            val role = createRole(guild, roleData.getObject(i))
            roles[role.idLong] = role
        }
        val channels = data.getArray("channels")
        val guildView = api.guildsView
        guildView.writeLock().use { guildView.map.put(id, guild) }
        for (i in 0 until channels.length()) {
            val channelData = channels.getObject(i)
            createTextChannel(guild, channelData)
        }
        return guild
    }

    private fun createUser(data: DataObject): User {
        val id = data.getLong("id")
        return userCache.getOrPut(id){
            val name = data.getString("username")
            val isBot = data.getBoolean("bot")
            User(id, api, name, isBot)
        }
    }

    private fun createMember(guild: Guild, memberData: DataObject): Member {
        val user = createUser(memberData.getObject("user"))
        val member = guild.getMemberOrPut(user){ Member(guild, user) }
        if (memberData.hasKey("nick")) {
            val lastNickName = member.nickname
            val nickName = memberData.getString("nick", null)
            if (nickName != lastNickName) {
                member.nickname = nickName
            }
        }
        return member
    }

    private fun createTextChannel(guild: Guild, channelData: DataObject) {
        val channelId = channelData.getLong("id")
        val name = channelData.getString("name")
        val textChannel = TextChannel(channelId, guild, name)
        guild.addTextChannel(channelId, textChannel)
    }

    private fun createRole(
        guild: Guild, roleJson: DataObject
    ): Role {
        var playbackCache = false
        val id = roleJson.getLong("id")
        var role = guild.rolesView[id]
        if (role == null) {
            val roleView = guild.rolesView
            roleView.writeLock().use {
                role = Role(id)
                playbackCache = roleView.map.put(id, role) == null
            }
        }
        role.name = roleJson.getString("name")
        if (playbackCache) api.eventCache.playbackCache(EventCache.Type.ROLE, id)
        return role
    }

    fun createMessage(jsonObject: DataObject, modifyCache: Boolean): Message {
        val channelId = jsonObject.getLong("channel_id")
        val channel = api.getTextChannelById(channelId) ?: throw IllegalArgumentException(MISSING_CHANNEL)
        return createMessage(jsonObject, channel, modifyCache)
    }

    fun createMessage(
        messageData: DataObject, channel: TextChannel, modifyCache: Boolean
    ): Message {
        val id = messageData.getLong("id")
        val authorData = messageData.getObject("author")
        val authorId = authorData.getLong("id")
        var member: Member? = null
        if (!messageData.isNull("member") && modifyCache) {
            val guild = channel.guild
            val cachedMember = guild.getMember(authorId)
            member = if (cachedMember == null) {
                val memberJson = messageData.getObject("member")
                memberJson.put("user", authorData)
                createMember(guild, memberJson)
            } else {
                cachedMember
            }
        }
        val content = messageData.getString("content", "")
        val fromWebhook = messageData.hasKey("webhook_id")
        val user = member?.user ?: createUser(authorData)
        val guild = channel.guild
        if (member == null) member = guild.getMember(authorId)
        if (modifyCache && !fromWebhook) {
            val lastName = user.name
            val name = authorData.getString("username")
            if (name != lastName) {
                user.name = name
            }
        }
        val mentionedRoles: TLongSet = TLongHashSet()
        val mentionedUsers: TLongSet = TLongHashSet(map(messageData, "mentions", Function { o: DataObject -> o.getLong("id") }))
        val roleMentionArray = messageData.optArray("mention_roles")
        roleMentionArray.ifPresent { array: DataArray ->
            for (i in 0 until array.length()) {
                mentionedRoles.add(array.getLong(i))
            }
        }
        val message = if (Check.isDefaultMessage(messageData.getInt("type"))) {
            Message(
                id, channel, mentionedUsers, mentionedRoles, content, user, member
            )
        } else {
            throw IllegalArgumentException(UNKNOWN_MESSAGE_TYPE)
        }
        val guildImpl = message.guild
        val mentionedUsersList: MutableList<User> = ArrayList()
        val mentionedMembersList: MutableList<Member> = ArrayList()
        val userMentions = messageData.getArray("mentions")
        for (i in 0 until userMentions.length()) {
            val mentionJson = userMentions.getObject(i)
            if (mentionJson.isNull("member")) {
                val mentionedUser = createUser(mentionJson)
                mentionedUsersList.add(mentionedUser)
                val mentionedMember = guildImpl.getMember(mentionedUser)
                if (mentionedMember != null) mentionedMembersList.add(mentionedMember)
            } else {
                val memberJson = mentionJson.getObject("member")
                mentionJson.remove("member")
                memberJson.put("user", mentionJson)
                val mentionedMember = createMember(guildImpl, memberJson)
                mentionedMembersList.add(mentionedMember)
                mentionedUsersList.add(mentionedMember.user)
            }
        }
        if (mentionedUsersList.isNotEmpty()) message.setMentions(mentionedUsersList, mentionedMembersList)
        return message
    }

    private fun <T> map(
        jsonObject: DataObject, key: String, convert: Function<DataObject, T>
    ): List<T> {
        if (jsonObject.isNull(key)) return emptyList()
        val array = jsonObject.getArray(key)
        val mappedObjects: MutableList<T> = ArrayList(array.length())
        for (i in 0 until array.length()) {
            val obj = array.getObject(i)
            val result: T? = convert.apply(obj)
            if (result != null) mappedObjects.add(result)
        }
        return mappedObjects
    }

    companion object {
        const val MISSING_CHANNEL = "MISSING_CHANNEL"
        const val MISSING_USER = "MISSING_USER"
        const val UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE"
    }

}