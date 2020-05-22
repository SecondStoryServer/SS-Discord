package me.syari.ss.discord

import me.syari.ss.discord.entities.Member
import me.syari.ss.discord.entities.Message
import me.syari.ss.discord.entities.TextChannel
import me.syari.ss.discord.entities.User

class MessageReceivedEvent(val message: Message) {
    val channel: TextChannel = message.channel

    val author: User
        get() = message.author

    val member: Member?
        get() = message.member
}