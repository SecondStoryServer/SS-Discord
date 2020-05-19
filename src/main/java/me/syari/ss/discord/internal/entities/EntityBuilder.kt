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

    fun createGuild(id: Long, data: DataObject, memberCount: Int): Guild {
        val name = data.getString("name", "")
        val roleData = data.getArray("roles")
        val guild = Guild(api, id, name, memberCount)
        guildCache[id] = guild
        val roles = mutableMapOf<Long, Role>()
        for(i in 0 until roleData.length()){
            val role = createRole(guild, roleData.getObject(i))
            roles[role.idLong] = role
        }
        val channelData = data.getArray("channels")
        val guildView = api.guildsView
        guildView.writeLock().use { guildView.map.put(id, guild) }
        for (i in 0 until channelData.length()) {
            val channelJson = channelData.getObject(i)
            createTextChannel(guild, channelJson)
        }
        return guild
    }

    private fun createFakeUser(user: DataObject): User {
        return createUser(user, true)
    }

    private fun createUser(user: DataObject): User {
        return createUser(user, false)
    }

    private fun createUser(userData: DataObject, fake: Boolean): User {
        val id = userData.getLong("id")
        val user = userCache.getOrPut(id){ User(id, api, fake) }
        if (!fake || user.isFake) {
            user.setName(userData.getString("username"))
            user.setDiscriminator(userData["discriminator"].toString())
            user.isBot = userData.getBoolean("bot")
        } else if (!user.isFake) {
            updateUser(user, userData)
        }
        if (!fake) api.eventCache.playbackCache(EventCache.Type.USER, id)
        return user
    }

    private fun updateUser(user: User, userData: DataObject) {
        val lastName = user.getName()
        val name = userData.getString("username")
        if (name != lastName) {
            user.setName(name)
        }
        val lastDiscriminator = user.getDiscriminator()
        val discriminator = userData["discriminator"].toString()
        if (discriminator != lastDiscriminator) {
            user.setDiscriminator(discriminator)
        }
    }

    private fun createMember(guild: Guild, memberJson: DataObject): Member {
        var playbackCache = false
        val user = createUser(memberJson.getObject("user"))
        var member = guild.getMember(user)
        if (member == null) {
            val memberView = guild.membersView
            memberView.writeLock().use {
                member = Member(guild, user)
                playbackCache = true
            }
        }
        if (playbackCache) {
            loadMember(memberJson, member!!)
            val hashId = guild.idLong xor user.idLong
            api.eventCache.playbackCache(EventCache.Type.MEMBER, hashId)
            guild.acknowledgeMembers()
        } else {
            updateMember(member!!, memberJson)
        }
        return member!!
    }

    private fun loadMember(memberJson: DataObject, member: Member) {
        member.nickname = memberJson.getString("nick", null)
    }

    private fun updateMember(member: Member, content: DataObject) {
        if (content.hasKey("nick")) {
            val lastNickName = member.nickname
            val nickName = content.getString("nick", null)
            if (nickName != lastNickName) {
                member.nickname = nickName
            }
        }
    }

    private fun createTextChannel(guildObj: Guild, channelData: DataObject) {
        if (Check.isTextChannel(channelData.getInt("type"))) {
            createTextChannel(guildObj, channelData, guildObj.idLong)
        }
    }

    private fun createTextChannel(guild: Guild, json: DataObject, guildId: Long) {
        var playbackCache = false
        val id = json.getLong("id")
        var channel = api.textChannelsView[id]
        if (channel == null) {
            val guildTextView = guild.textChannelsView
            val textView = api.textChannelsView
            guildTextView.writeLock().use {
                textView.writeLock().use {
                    channel = TextChannel(id, guild)
                    guildTextView.map.put(id, channel)
                    playbackCache = textView.map.put(id, channel) == null
                }
            }
        }
        channel.setName(json.getString("name"))
        if (playbackCache) api.eventCache.playbackCache(EventCache.Type.CHANNEL, id)
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
        jsonObject: DataObject, channel: TextChannel, modifyCache: Boolean
    ): Message {
        val id = jsonObject.getLong("id")
        val author = jsonObject.getObject("author")
        val authorId = author.getLong("id")
        var member: Member? = null
        if (!jsonObject.isNull("member") && modifyCache) {
            val guild = channel.getGuild()
            val cachedMember = guild.getMemberById(authorId)
            member = if (cachedMember == null) {
                val memberJson = jsonObject.getObject("member")
                memberJson.put("user", author)
                createMember(guild, memberJson)
            } else {
                cachedMember
            }
        }
        val content = jsonObject.getString("content", "")
        val fromWebhook = jsonObject.hasKey("webhook_id")
        var user: User?
        val guild = channel.getGuild()
        if (member == null) member = guild.getMemberById(authorId)
        user = member?.user
        if (user == null) {
            user = if (fromWebhook || !modifyCache) {
                createFakeUser(author)
            } else {
                throw IllegalArgumentException(MISSING_USER)
            }
        }
        if (modifyCache && !fromWebhook) updateUser(user, author)
        val mentionedRoles: TLongSet = TLongHashSet()
        val mentionedUsers: TLongSet =
            TLongHashSet(map(jsonObject, "mentions", Function { o: DataObject -> o.getLong("id") }))
        val roleMentionArray = jsonObject.optArray("mention_roles")
        roleMentionArray.ifPresent { array: DataArray ->
            for (i in 0 until array.length()) {
                mentionedRoles.add(array.getLong(i))
            }
        }
        val message: Message
        message = if (Check.isDefaultMessage(jsonObject.getInt("type"))) {
            Message(
                id, channel, mentionedUsers, mentionedRoles, content, user, member
            )
        } else {
            throw IllegalArgumentException(UNKNOWN_MESSAGE_TYPE)
        }
        val guildImpl = message.guild
        if (guildImpl.isLoaded) return message
        val mentionedUsersList: MutableList<User> = ArrayList()
        val mentionedMembersList: MutableList<Member> = ArrayList()
        val userMentions = jsonObject.getArray("mentions")
        for (i in 0 until userMentions.length()) {
            val mentionJson = userMentions.getObject(i)
            if (mentionJson.isNull("member")) {
                val mentionedUser = createFakeUser(mentionJson)
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