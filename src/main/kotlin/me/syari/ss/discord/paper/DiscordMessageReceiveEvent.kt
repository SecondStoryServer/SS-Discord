package me.syari.ss.discord.paper

import me.syari.ss.core.event.CustomEvent
import me.syari.ss.discord.MessageReceivedEvent

class DiscordMessageReceiveEvent(event: MessageReceivedEvent): CustomEvent() {
    val channel = event.channel
    val author = event.author
    val member = event.member
    val content = event.content
    val contentDisplay = event.contentDisplay
}