package me.syari.ss.discord.api.utils.cache

import me.syari.ss.discord.api.ISnowflake

interface ISnowflakeCacheView<T: ISnowflake?>: CacheView<T> {
    fun getElementById(id: Long): T?
}