package me.syari.ss.discord.handle

import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import me.syari.ss.discord.data.DataContainer
import java.util.concurrent.atomic.AtomicInteger

object EventCache {
    const val TIMEOUT_AMOUNT: Long = 100

    private val eventCache = mutableMapOf<Type, TLongObjectMap<MutableList<CacheNode>>>()

    @Synchronized
    fun timeout(responseTotal: Long) {
        if (eventCache.isEmpty()) return
        val count = AtomicInteger()
        eventCache.forEach { (_: Type?, map: TLongObjectMap<MutableList<CacheNode>>) ->
            if (map.isEmpty) return@forEach
            val iterator = map.iterator()
            while (iterator.hasNext()) {
                iterator.advance()
                val cache = iterator.value()
                cache.removeIf { node: CacheNode ->
                    val remove = responseTotal - node.responseTotal > TIMEOUT_AMOUNT
                    if (remove) count.incrementAndGet()
                    remove
                }
                if (cache.isEmpty()) iterator.remove()
            }
        }
    }

    @Synchronized
    fun cache(
        type: Type, triggerId: Long, responseTotal: Long, event: DataContainer, handler: (Long, DataContainer) -> Unit
    ) {
        val triggerCache = eventCache.computeIfAbsent(type) { TLongObjectHashMap() }
        var items = triggerCache[triggerId]
        if (items == null) {
            items = mutableListOf()
            triggerCache.put(triggerId, items)
        }
        items.add(CacheNode(responseTotal, event, handler))
    }

    @Synchronized
    fun playbackCache(type: Type, triggerId: Long) {
        val typeCache = eventCache[type] ?: return
        val items: List<CacheNode>? = typeCache.remove(triggerId)
        if (items != null && items.isNotEmpty()) {
            for (item in items) {
                item.execute()
            }
        }
    }

    @Synchronized
    fun clear() {
        eventCache.clear()
    }

    enum class Type {
        GUILD,
        CHANNEL
    }

    private class CacheNode(
        val responseTotal: Long, private val event: DataContainer, private val callback: (Long, DataContainer) -> Unit
    ) {
        fun execute() {
            callback.invoke(responseTotal, event)
        }

    }
}