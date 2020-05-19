package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView
import me.syari.ss.discord.internal.JDA
import me.syari.ss.discord.internal.utils.cache.MemberCacheView
import me.syari.ss.discord.internal.utils.cache.SnowflakeCacheView
import java.util.concurrent.CompletableFuture

class Guild(val jDA: JDA, override val idLong: Long, private val name: String, private val memberCount: Int): ISnowflake {
    val textChannelsView = SnowflakeCacheView(TextChannel::class.java)
    val rolesView = SnowflakeCacheView(Role::class.java)
    val emoteCache = SnowflakeCacheView(Emote::class.java)
    val membersView = MemberCacheView()
    private val chunkingCallback = CompletableFuture<Void?>()
    val isLoaded: Boolean
        get() = memberCount.toLong() <= membersView.size()

    fun getMember(user: User): Member? {
        return getMemberById(user.idLong)
    }

    private fun getRoleCache(): ISnowflakeCacheView<Role> {
        return rolesView
    }

    fun getMemberById(userId: Long): Member? {
        return membersView.getElementById(userId)
    }

    fun getRoleById(id: Long): Role? {
        return getRoleCache().getElementById(id)
    }

    fun acknowledgeMembers() {
        if (membersView.size() == memberCount.toLong() && !chunkingCallback.isDone) chunkingCallback.complete(null)
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