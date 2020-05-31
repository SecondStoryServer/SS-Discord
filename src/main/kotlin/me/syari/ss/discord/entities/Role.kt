package me.syari.ss.discord.entities

data class Role(
    override val idLong: Long, val name: String
): WithId, Mentionable {
    companion object {
        private val roleList = mutableListOf<Role>()

        fun add(role: Role) {
            roleList.add(role)
        }

        fun get(id: Long): Role? {
            return roleList.firstOrNull { it.idLong == id }
        }
    }

    init {
        add(this)
    }

    override val asMention = "<@&$idLong>"

    override val asDisplay = "@$name"
}