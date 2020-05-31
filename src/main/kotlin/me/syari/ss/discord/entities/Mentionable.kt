package me.syari.ss.discord.entities

interface Mentionable {
    val asMention: String

    val asDisplay: String
}