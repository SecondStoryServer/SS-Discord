package me.syari.ss.discord.api

interface WithId {
    val id: String
        get() = java.lang.Long.toUnsignedString(idLong)

    val idLong: Long
}