package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.internal.JDA

class User(override val idLong: Long, val api: JDA, var name: String, val isBot: Boolean): ISnowflake {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is User) return false
        return idLong == other.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "User:$name($idLong)"
    }
}