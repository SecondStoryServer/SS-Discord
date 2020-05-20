package me.syari.ss.discord.internal.entities

import me.syari.ss.discord.api.WithId

class Role(override val idLong: Long, val name: String): WithId {
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

    val asMention = "<@&$idLong>"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Role) return false
        return idLong == other.idLong
    }

    override fun hashCode(): Int {
        return java.lang.Long.hashCode(idLong)
    }

    override fun toString(): String {
        return "Role:$name($idLong)"
    }
}