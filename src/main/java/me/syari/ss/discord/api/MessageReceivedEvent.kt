package me.syari.ss.discord.api

import me.syari.ss.discord.internal.entities.Member
import me.syari.ss.discord.internal.entities.Message
import me.syari.ss.discord.internal.entities.TextChannel
import me.syari.ss.discord.internal.entities.User

class MessageReceivedEvent(val message: Message) {
    val channel: TextChannel = message.channel

    val author: User
        get() = message.author

    val member: Member?
        get() = message.member
}