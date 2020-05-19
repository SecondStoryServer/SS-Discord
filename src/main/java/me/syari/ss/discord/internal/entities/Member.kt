package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.internal.utils.cache.SnowflakeReference
import java.util.function.LongFunction

class Member(guild: Guild?, user: User): ISnowflake {
    private val guild: SnowflakeReference<Guild?>
    val user: User
    var nickname: String? = null

    private fun getGuild(): Guild {
        return guild.resolve()
    }

    val displayName: String
        get() = (if (nickname != null) nickname else user.name) ?: "null"

    override val idLong: Long
        get() = user.idLong

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Member) return false
        return other.user.idLong == user.idLong && other.guild.idLong == guild.idLong
    }

    override fun hashCode(): Int {
        return (guild.idLong.toString() + user.idLong.toString()).hashCode()
    }

    override fun toString(): String {
        return "Member:" + displayName + '(' + user.toString() + " / " + getGuild().toString() + ')'
    }

    init {
        this.guild = SnowflakeReference(guild, LongFunction { id: Long -> user.api.getGuildById(id) })
        this.user = user
    }
}