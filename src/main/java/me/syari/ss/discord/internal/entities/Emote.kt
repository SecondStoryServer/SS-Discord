package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake

class Emote(override val idLong: Long): ISnowflake {
    var isAnimated = false
    var name = ""

    val asMention: String
        get() = (if (isAnimated) "<a:" else "<:") + name + ":" + id + ">"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Emote) return false
        return idLong == other.idLong && name == other.name
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "E:$name($idLong)"
    }
}