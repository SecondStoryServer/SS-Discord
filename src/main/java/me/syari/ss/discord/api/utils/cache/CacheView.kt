package me.syari.ss.discord.api.utils.cache

import me.syari.ss.discord.api.ISnowflake
import me.syari.ss.discord.api.utils.ClosableIterator
import me.syari.ss.discord.internal.utils.cache.UnifiedSnowflakeCacheView
import java.util.function.Supplier
import java.util.stream.Stream

interface CacheView<T>: Iterable<T> {
    fun lockedIterator(): ClosableIterator<T>
    val isEmpty: Boolean
    fun stream(): Stream<T>

    companion object {
        @JvmStatic
        fun <E: ISnowflake?> allSnowflakes(generator: Supplier<out Stream<out ISnowflakeCacheView<E>?>?>): ISnowflakeCacheView<E> {
            return UnifiedSnowflakeCacheView(generator)
        }
    }
}