package me.syari.ss.discord.api.exceptions

import java.util.function.Consumer

object ContextException: Exception() {
    @JvmStatic
    fun from(acceptor: Consumer<in Throwable>): Consumer<Throwable> {
        return ContextConsumer(ContextException, acceptor)
    }

    class ContextConsumer(
        private val context: ContextException, private val callback: Consumer<in Throwable>
    ): Consumer<Throwable> {
        override fun accept(throwable: Throwable) {
            var cause: Throwable? = throwable
            while (cause?.cause != null) {
                cause = cause.cause
            }
            cause?.initCause(context)
            callback.accept(throwable)
        }
    }
}