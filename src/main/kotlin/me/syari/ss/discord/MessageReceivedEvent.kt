package me.syari.ss.discord

import me.syari.ss.discord.entities.Message

class MessageReceivedEvent(message: Message) {
    val channel = message.channel
    val author = message.author
    val member = message.member
    val content = message.content
    val contentDisplay = message.contentDisplay
}