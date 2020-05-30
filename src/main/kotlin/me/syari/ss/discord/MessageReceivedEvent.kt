package me.syari.ss.discord

import me.syari.ss.discord.entities.Message

class MessageReceivedEvent(message: Message) {
    val channel = message.channel
    val user = message.authorUser
    val member = message.authorMember
    val content = message.content
    val contentDisplay = message.contentDisplay
}