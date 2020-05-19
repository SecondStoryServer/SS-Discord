package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.internal.JDA

class User(override val idLong: Long, val api: JDA, var isFake: Boolean): ISnowflake {
    private var discriminator: Short = 0
    private var name: String? = null
    var isBot = false
    fun getName(): String {
        return name!!
    }

    fun getDiscriminator(): String {
        return String.format("%04d", discriminator)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is User) return false
        return idLong == other.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "U:" + getName() + '(' + idLong + ')'
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun setDiscriminator(discriminator: String) {
        this.discriminator = discriminator.toShort()
    }

}