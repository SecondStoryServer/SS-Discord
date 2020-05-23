package me.syari.ss.discord.entities

import gnu.trove.set.hash.TLongHashSet
import me.syari.ss.discord.data.DataContainer

object EntityBuilder {
    const val MISSING_CHANNEL = "MISSING_CHANNEL"
    const val UNKNOWN_MESSAGE_TYPE = "UNKNOWN_MESSAGE_TYPE"

    fun createGuild(id: Long, guildData: DataContainer): Guild {
        val name = guildData.getString("name") ?: ""
        val allRole = guildData.getArrayOrThrow("roles")
        val guild = Guild(id, name)
        val roles = mutableMapOf<Long, Role>()
        for (i in 0 until allRole.size) {
            val role = createRole(guild, allRole.getContainerOrThrow(i))
            roles[role.idLong] = role
        }
        val allChannel = guildData.getArrayOrThrow("channels")
        for (i in 0 until allChannel.size) {
            val channelData = allChannel.getContainerOrThrow(i)
            createTextChannel(guild, channelData)
        }
        return guild
    }

    private fun createUser(userData: DataContainer): User {
        val id = userData.getLongOrThrow("id")
        return User.get(id) {
            val name = userData.getStringOrThrow("username")
            val isBot = userData.getBoolean("bot") ?: false
            User(id, name, isBot)
        }
    }

    private fun createMember(guild: Guild, memberData: DataContainer): Member {
        val user = createUser(memberData.getContainerOrThrow("user"))
        val member = guild.getMemberOrPut(user) { Member(guild, user) }
        if (memberData.contains("nick")) {
            val lastNickName = member.nickname
            val nickName = memberData.getString("nick")
            if (nickName != lastNickName) {
                member.nickname = nickName
            }
        }
        return member
    }

    private fun createTextChannel(guild: Guild, channelData: DataContainer) {
        val channelId = channelData.getLongOrThrow("id")
        val name = channelData.getStringOrThrow("name")
        val textChannel = TextChannel(channelId, guild, name)
        guild.addTextChannel(channelId, textChannel)
    }

    private fun createRole(
        guild: Guild, roleData: DataContainer
    ): Role {
        val id = roleData.getLongOrThrow("id")
        return guild.getRoleOrPut(id) {
            val name = roleData.getStringOrThrow("name")
            Role(id, name)
        }
    }

    fun createMessage(
        messageData: DataContainer, channel: TextChannel
    ): Message {
        val id = messageData.getLongOrThrow("id")
        val authorData = messageData.getContainerOrThrow("author")
        val guild = channel.guild
        val member = guild.getMemberOrPut(id) {
            val memberData = messageData.getContainerOrThrow("member")
            memberData.put("user", authorData)
            createMember(guild, memberData)
        }
        val fromWebhook = messageData.contains("webhook_id")
        val user = member.user
        if (!fromWebhook) {
            val lastName = user.name
            val name = authorData.getStringOrThrow("username")
            if (name != lastName) {
                user.name = name
            }
        }
        val mentionedRoles = TLongHashSet()
        val roleMentionArray = messageData.getArray("mention_roles")
        if (roleMentionArray != null) {
            for (i in 0 until roleMentionArray.size) {
                mentionedRoles.add(roleMentionArray.getLongOrThrow(i))
            }
        }
        val mentionedUsersList = mutableListOf<User>()
        val mentionedMembersList = mutableListOf<Member>()
        val userMentions = messageData.getArray("mentions")
        if (userMentions != null) {
            for (i in 0 until userMentions.size) {
                val mentionData = userMentions.getContainerOrThrow(i)
                val mentionedMemberData = mentionData.getContainer("member")
                if (mentionedMemberData != null) {
                    mentionData.remove("member")
                    mentionedMemberData.put("user", mentionData)
                    val mentionedMember = createMember(guild, mentionedMemberData)
                    mentionedMembersList.add(mentionedMember)
                    mentionedUsersList.add(mentionedMember.user)
                } else {
                    val mentionedUser = createUser(mentionData)
                    mentionedUsersList.add(mentionedUser)
                    val mentionedMember = guild.getMember(mentionedUser)
                    if (mentionedMember != null) mentionedMembersList.add(mentionedMember)
                }
            }
        }
        val content = messageData.getString("content") ?: ""
        return Message(channel, content, user, member)
    }
}