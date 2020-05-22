package me.syari.ss.discord.internal.entities

import java.lang.Long.toUnsignedString

interface WithId {
    val id: String
        get() = toUnsignedString(idLong)

    val idLong: Long
}