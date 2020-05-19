package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheView
import java.util.concurrent.CompletableFuture

class Guild(
    val api: JDA,
    override val idLong: Long,
    private val name: String
): ISnowflake {
    companion object {
        val guildList = mutableListOf<Guild>()

        fun add(guild: Guild){
            guildList.add(guild)
        }
    }

    val rolesView = SnowflakeCacheView(Role::class.java)
    val emoteCache = SnowflakeCacheView(Emote::class.java)

    private fun getRoleCache(): ISnowflakeCacheView<Role> {
        return rolesView
    }

    private val textChannelCache = mutableMapOf<Long, TextChannel>()

    fun addTextChannel(id: Long, textChannel: TextChannel){
        textChannelCache[id] = textChannel
    }

    fun getTextChannel(id: Long): TextChannel? {
        return textChannelCache[id]
    }

    private val memberCache = mutableMapOf<Long, Member>()

    private fun getMemberOrPut(id: Long, run: () -> Member): Member {
        return memberCache.getOrPut(id, run)
    }

    fun getMember(id: Long): Member? {
        return memberCache[id]
    }

    fun getMemberOrPut(user: User, run: () -> Member): Member {
        return getMemberOrPut(user.idLong, run)
    }

    fun getMember(user: User): Member? {
        return getMember(user.idLong)
    }

    fun getRoleById(id: Long): Role? {
        return getRoleCache().getElementById(id)
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
        return "G:$name($id)"
    }

}