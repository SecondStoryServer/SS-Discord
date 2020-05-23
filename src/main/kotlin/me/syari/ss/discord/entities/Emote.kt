package me.syari.ss.discord.entities

data class Emote(
    override val idLong: Long, val name: String, private val isAnimated: Boolean
): WithId {
    companion object {
        private val emoteList = mutableMapOf<Long, Emote>()

        fun get(id: Long): Emote? {
            return emoteList[id]
        }

        fun get(id: Long, run: () -> Emote): Emote {
            return get(id) ?: run.invoke()
        }
    }

    init {
        emoteList[idLong] = this
    }

    val asMention: String
        get() = (if (isAnimated) "<a:" else "<:") + name + ":" + id + ">"
}