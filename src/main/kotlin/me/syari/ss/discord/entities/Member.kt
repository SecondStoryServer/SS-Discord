package me.syari.ss.discord.entities

data class Member(
    val guild: Guild, val user: User
): WithId {
    var nickname: String? = null

    val displayName
        get() = (if (nickname != null) nickname else user.name) ?: "null"

    override val idLong
        get() = user.idLong
}