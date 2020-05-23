package me.syari.ss.discord.entities

data class User(
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
}