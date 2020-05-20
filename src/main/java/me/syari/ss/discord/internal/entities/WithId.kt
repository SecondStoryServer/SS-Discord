package me.syari.ss.discord.internal.entities

interface WithId {
    val id: String
        get() = java.lang.Long.toUnsignedString(idLong)

    val idLong: Long
}