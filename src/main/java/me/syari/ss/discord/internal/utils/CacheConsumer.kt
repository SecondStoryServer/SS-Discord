package me.syari.ss.discord.internal.utils

import me.syari.ss.discord.api.utils.data.DataObject

@FunctionalInterface
interface CacheConsumer {
    fun execute(responseTotal: Long, allContent: DataObject?)
}