package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.internal.JDA

class Guild(
    val api: JDA, override val idLong: Long, private val name: String
): ISnowflake {
    companion object {
        private val guildList = mutableMapOf<Long, Guild>()

        fun contains(id: Long): Boolean {
            return guildList.containsKey(id)
        }

        fun get(id: Long): Guild? {
            return guildList[id]
        }

        val allGuild
            get() = guildList.values
    }

    init {
        guildList[idLong] = this
    }

    private val textChannelCache = mutableMapOf<Long, TextChannel>()

    fun addTextChannel(id: Long, textChannel: TextChannel) {
        textChannelCache[id] = textChannel
    }

    fun getTextChannel(id: Long): TextChannel? {
        return textChannelCache[id]
    }

    private val memberCache = mutableMapOf<Long, Member>()

    fun getMemberOrPut(id: Long, run: () -> Member): Member {
        return memberCache.getOrPut(id, run)
    }

    private fun getMember(id: Long): Member? {
        return memberCache[id]
    }

    fun getMemberOrPut(user: User, run: () -> Member): Member {
        return getMemberOrPut(user.idLong, run)
    }

    fun getMember(user: User): Member? {
        return getMember(user.idLong)
    }

    private val roleCache = mutableMapOf<Long, Role>()

    fun getRoleOrPut(id: Long, run: () -> Role): Role {
        return roleCache.getOrPut(id, run)
    }

    fun getRole(id: Long): Role? {
        return roleCache[id]
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Guild) return false
        return idLong == other.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "Guild:$name($id)"
    }

}