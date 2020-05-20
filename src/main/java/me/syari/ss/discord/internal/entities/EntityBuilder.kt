package me.syari.ss.discord.internal.entities

import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.api.utils.data.DataObject
import me.syari.ss.discord.internal.JDA

class EntityBuilder(private val api: JDA) {
    fun createGuild(id: Long, guildData: DataObject): Guild {
        val name = guildData.getString("name", "")
        val allRole = guildData.getArray("roles")
        val guild = Guild(api, id, name)
        val roles = mutableMapOf<Long, Role>()
        for (i in 0 until allRole.length()) {
            val role = createRole(guild, allRole.getObject(i))
            roles[role.idLong] = role
        }
        val allChannel = guildData.getArray("channels")
        for (i in 0 until allChannel.length()) {
            val channelData = allChannel.getObject(i)
            createTextChannel(guild, channelData)
        }
        return guild
    }

    private fun createUser(userData: DataObject): User {
        val id = userData.getLong("id")
        return User.get(id) {
            val name = userData.getString("username")
            val isBot = userData.getBoolean("bot", false)
            User(id, api, name, isBot)
        }
    }

    private fun createMember(guild: Guild, memberData: DataObject): Member {
        val user = createUser(memberData.getObject("user"))
        val member = guild.getMemberOrPut(user) { Member(guild, user) }
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
        guild: Guild, roleData: DataObject
    ): Role {
        val id = roleData.getLong("id")
        return guild.getRoleOrPut(id) {
            val name = roleData.getString("name")
            Role(id, name)
        }
    }

    fun createMessage(
        messageData: DataObject, channel: TextChannel
    ): Message {
        val id = messageData.getLong("id")
        val authorData = messageData.getObject("author")
        val guild = channel.guild
        val member = guild.getMemberOrPut(id) {
            val memberData = messageData.getObject("member")
            memberData.put("user", authorData)
            createMember(guild, memberData)
        }
        val fromWebhook = messageData.hasKey("webhook_id")
        val user = member.user
        if (!fromWebhook) {
            val lastName = user.name
            val name = authorData.getString("username")
            if (name != lastName) {
                user.name = name
            }
        }
        val mentionedRoles = TLongHashSet()
        val roleMentionArray = messageData.optArray("mention_roles")
        roleMentionArray.ifPresent { array ->
            for (i in 0 until array.length()) {
                mentionedRoles.add(array.getLong(i))
            }
        }
        val mentionedUsersList = mutableListOf<User>()
        val mentionedMembersList = mutableListOf<Member>()
        val userMentions = messageData.getArray("mentions")
        for (i in 0 until userMentions.length()) {
            val mentionData = userMentions.getObject(i)
            if (mentionData.isNull("member")) {
                val mentionedUser = createUser(mentionData)
                mentionedUsersList.add(mentionedUser)
                val mentionedMember = guild.getMember(mentionedUser)
                if (mentionedMember != null) mentionedMembersList.add(mentionedMember)
            } else {
                val mentionedMemberData = mentionData.getObject("member")
                mentionData.remove("member")
                mentionedMemberData.put("user", mentionData)
                val mentionedMember = createMember(guild, mentionedMemberData)
                mentionedMembersList.add(mentionedMember)
                mentionedUsersList.add(mentionedMember.user)
            }
        }
        val content = messageData.getString("content", "")
        return Message(id, channel, content, user, member)
    }

    companion object {
        const val MISSING_CHANNEL = "MISSING_CHANNEL"
        const val UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE"
    }

}