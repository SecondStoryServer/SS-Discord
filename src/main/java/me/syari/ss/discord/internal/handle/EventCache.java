package me.syari.ss.discord.internal.handle;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.utils.CacheConsumer;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventCache {
    public static final Logger LOG = JDALogger.getLog(EventCache.class);

    public static final long TIMEOUT_AMOUNT = 100;
    private final EnumMap<Type, TLongObjectMap<List<CacheNode>>> eventCache = new EnumMap<>(Type.class);
    private final boolean cacheUsers;

    public EventCache(boolean cacheUsers) {
        this.cacheUsers = cacheUsers;
    }

    public synchronized void timeout(final long responseTotal) {
        if (eventCache.isEmpty())
            return;
        AtomicInteger count = new AtomicInteger();
        eventCache.forEach((type, map) ->
        {
            if (map.isEmpty())
                return;
            TLongObjectIterator<List<CacheNode>> iterator = map.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                long triggerId = iterator.key();
                List<CacheNode> cache = iterator.value();
                //Remove when this node is more than 100 events ago
                cache.removeIf(node ->
                {
                    boolean remove = responseTotal - node.responseTotal > TIMEOUT_AMOUNT;
                    if (remove) {
                        count.incrementAndGet();
                        LOG.trace("Removing type {}/{} from event cache with payload {}", type, triggerId, node.event);
                    }
                    return remove;
                });
                if (cache.isEmpty())
                    iterator.remove();
            }
        });
        int amount = count.get();
        if (amount > 0)
            LOG.debug("Removed {} events from cache that were too old to be recycled", amount);
    }

    public synchronized void cache(Type type, long triggerId, long responseTotal, DataObject event, CacheConsumer handler) {
        if (type == Type.USER && !cacheUsers)
            return;
        TLongObjectMap<List<CacheNode>> triggerCache =
                eventCache.computeIfAbsent(type, k -> new TLongObjectHashMap<>());

        List<CacheNode> items = triggerCache.get(triggerId);
        if (items == null) {
            items = new LinkedList<>();
            triggerCache.put(triggerId, items);
        }

        items.add(new CacheNode(responseTotal, event, handler));
    }

    public synchronized void playbackCache(Type type, long triggerId) {
        TLongObjectMap<List<CacheNode>> typeCache = this.eventCache.get(type);
        if (typeCache == null)
            return;

        List<CacheNode> items = typeCache.remove(triggerId);
        if (items != null && !items.isEmpty()) {
            EventCache.LOG.debug("Replaying {} events from the EventCache for type {} with id: {}",
                    items.size(), type, triggerId);
            for (CacheNode item : items)
                item.execute();
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
