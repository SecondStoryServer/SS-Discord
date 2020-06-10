package me.syari.ss.discord.paper

import me.syari.discord.entity.api.Message
import me.syari.ss.core.event.CustomEvent

class DiscordMessageReceiveEvent(message: Message): CustomEvent() {
    val channel = message.channel
    val author = message.member
    val content = message.content
    val contentDisplay = message.contentDisplay
}