package me.syari.ss.discord.requests

internal class ThreadLocalReason private constructor() {
    class Closable(reason: String?): AutoCloseable {
        private val previous = current

        override fun close() {
            current = previous
        }

        init {
            current = reason
        }
    }

    companion object {
        private var currentReason: ThreadLocal<String>? = null

        var current: String?
            get() = currentReason?.get()
            set(reason) {
                if (reason != null) {
                    if (currentReason == null) {
                        currentReason = ThreadLocal()
                    }
                    currentReason?.set(reason)
                } else if (currentReason != null) {
                    currentReason?.remove()
                }
            }

        fun closable(reason: String?): Closable {
            return Closable(reason)
        }
    }
}