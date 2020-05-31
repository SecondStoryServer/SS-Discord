package me.syari.ss.discord.entities

data class Guild(
    override val idLong: Long, private val name: String
): WithId {
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

    fun getMember(id: Long): Member? {
        return memberCache[id]
    }

    private val roleCache = mutableMapOf<Long, Role>()

    fun getRoleOrPut(id: Long, run: () -> Role): Role {
        return roleCache.getOrPut(id, run)
    }

    fun getRole(id: Long): Role? {
        return roleCache[id]
    }
}