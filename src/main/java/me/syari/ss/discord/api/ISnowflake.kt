package me.syari.ss.discord.api

interface ISnowflake {
    val id: String
        get() = java.lang.Long.toUnsignedString(idLong)

    val idLong: Long
}