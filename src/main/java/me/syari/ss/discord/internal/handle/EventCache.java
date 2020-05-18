package me.syari.ss.discord.internal.handle;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.utils.CacheConsumer;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventCache {
    public static final long TIMEOUT_AMOUNT = 100;

    private final EnumMap<Type, TLongObjectMap<List<CacheNode>>> eventCache = new EnumMap<>(Type.class);

    public synchronized void timeout(final long responseTotal) {
        if (eventCache.isEmpty()) return;
        AtomicInteger count = new AtomicInteger();
        eventCache.forEach((type, map) ->
        {
            if (map.isEmpty()) return;
            TLongObjectIterator<List<CacheNode>> iterator = map.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                List<CacheNode> cache = iterator.value();
                cache.removeIf(node ->
                {
                    boolean remove = responseTotal - node.responseTotal > TIMEOUT_AMOUNT;
                    if (remove) count.incrementAndGet();
                    return remove;
                });
                if (cache.isEmpty()) iterator.remove();
            }
        });
    }

    public synchronized void cache(Type type, long triggerId, long responseTotal, DataObject event, CacheConsumer handler) {
        TLongObjectMap<List<CacheNode>> triggerCache = eventCache.computeIfAbsent(type, k -> new TLongObjectHashMap<>());
        List<CacheNode> items = triggerCache.get(triggerId);
        if (items == null) {
            items = new LinkedList<>();
            triggerCache.put(triggerId, items);
        }
        items.add(new CacheNode(responseTotal, event, handler));
    }

    public synchronized void playbackCache(Type type, long triggerId) {
        TLongObjectMap<List<CacheNode>> typeCache = this.eventCache.get(type);
        if (typeCache == null) return;
        List<CacheNode> items = typeCache.remove(triggerId);
        if (items != null && !items.isEmpty()) {
            for (CacheNode item : items) {
                item.execute();
            }
        }
    }

    public synchronized void clear() {
        eventCache.clear();
    }

    public enum Type {
        USER, MEMBER, GUILD, CHANNEL, ROLE
    }

    private static class CacheNode {
        private final long responseTotal;
        private final DataObject event;
        private final CacheConsumer callback;

        public CacheNode(long responseTotal, DataObject event, CacheConsumer callback) {
            this.responseTotal = responseTotal;
            this.event = event;
            this.callback = callback;
        }

        void execute() {
            callback.execute(responseTotal, event);
        }
    }
}
