package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake

class Role(override val idLong: Long): ISnowflake {
    var name: String? = null

    val asMention: String
        get() = "<@&" + idLong + '>'

    override fun equals(`object`: Any?): Boolean {
        if (`object` === this) return true
        if (`object` !is Role) return false
        return idLong == `object`.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "R:" + name + '(' + idLong + ')'
    }

}