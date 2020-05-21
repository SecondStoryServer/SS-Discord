package me.syari.ss.discord.internal.entities

class User(
    override val idLong: Long, var name: String, val isBot: Boolean
): WithId {
    companion object {
        private val userList = mutableMapOf<Long, User>()

        fun get(id: Long): User? {
            return userList[id]
        }

        fun get(id: Long, run: () -> User): User {
            return get(id) ?: run.invoke()
        }
    }

    init {
        userList[idLong] = this
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
        return "User:$name($idLong)"
    }
}