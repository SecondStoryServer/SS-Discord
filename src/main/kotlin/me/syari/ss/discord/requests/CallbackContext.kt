package me.syari.ss.discord.requests

internal class CallbackContext private constructor(): AutoCloseable {
    override fun close() {
        callback.set(false)
    }

    companion object {
        private val callback = ThreadLocal.withInitial { false }

        val instance = CallbackContext()
            get() {
                startCallback()
                return field
            }

        val isCallbackContext: Boolean
            get() = callback.get()

        private fun startCallback() {
            callback.set(true)
        }
    }
}