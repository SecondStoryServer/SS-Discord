package me.syari.ss.discord.entities

internal data class Emote(
    override val idLong: Long, val name: String, private val isAnimated: Boolean
): WithId, Mentionable {
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

    override val asMention = "${if (isAnimated) "<a:" else "<:"}$name:$id>"

    override val asDisplay = ":$name:"
}