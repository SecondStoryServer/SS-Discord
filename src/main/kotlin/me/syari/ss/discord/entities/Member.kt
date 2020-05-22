package me.syari.ss.discord.entities

class Member(
    val guild: Guild, val user: User
): WithId {
    var nickname: String? = null

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
        return "Member:$displayName($user/$guild)"
    }
}