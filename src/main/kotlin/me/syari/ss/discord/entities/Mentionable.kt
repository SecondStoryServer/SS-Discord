package me.syari.ss.discord.entities

interface Mentionable {
    val asMention: String

    val asDisplay: String

    fun replaceMentionAsDisplay(text: String): String {
        return text.replace(asMention, asDisplay)
    }
}