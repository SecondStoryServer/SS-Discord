package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake

class Role(override val idLong: Long, val name: String): ISnowflake {
    val asMention = "<@&$idLong>"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Role) return false
        return idLong == other.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "R:$name($idLong)"
    }

}