package me.syari.ss.discord.internal.handle;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.requests.WebSocketCode;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

@SuppressWarnings("WeakerAccess")
public class GuildSetupController {
    protected static final int CHUNK_TIMEOUT = 10000;
    protected static final Logger log = JDALogger.getLog(GuildSetupController.class);

    private final JDAImpl api;
    private final TLongObjectMap<GuildSetupNode> setupNodes = new TLongObjectHashMap<>();
    private final TLongSet chunkingGuilds = new TLongHashSet();
    private final TLongLongMap pendingChunks = new TLongLongHashMap();
    private final TLongSet syncingGuilds;
    private final TLongSet unavailableGuilds = new TLongHashSet();

    private int incompleteCount = 0;
    private int syncingCount = 0;

    private Future<?> timeoutHandle;

    protected final StatusListener listener = (id, oldStatus, newStatus) -> log.trace("[{}] Updated status {}->{}", id, oldStatus, newStatus);

    public GuildSetupController(JDAImpl api) {
        this.api = api;
        syncingGuilds = null;
    }

    JDAImpl getJDA() {
        return api;
    }

    void addGuildForChunking(long id, boolean join) {
        log.trace("Adding guild for chunking ID: {}", id);
        if (join || incompleteCount <= 0) {
            if (incompleteCount <= 0) {
                // this happens during runtime -> chunk right away
                sendChunkRequest(id);
                return;
            }
            incompleteCount++;
        }
        chunkingGuilds.add(id);
        tryChunking();
    }

    void addGuildForSyncing(long id, boolean join) {
        log.trace("Adding guild for syncing ID: {}", id);
        if (join || incompleteCount <= 0) {
            if (incompleteCount <= 0) {
                // this happens during runtime -> sync right away
                sendSyncRequest(DataArray.empty().add(id));
                return;
            }
            syncingCount++;
        }
        syncingGuilds.add(id);
        trySyncing();
    }

    void remove(long id) {
        unavailableGuilds.remove(id);
        setupNodes.remove(id);
        chunkingGuilds.remove(id);
        synchronized (pendingChunks) {
            pendingChunks.remove(id);
        }
        if (syncingGuilds != null)
            syncingGuilds.remove(id);
    }

    public void ready(long id) {
        remove(id);
        WebSocketClient client = getJDA().getClient();
        if (--incompleteCount < 1 && !client.isReady())
            client.ready();
        else
            tryChunking();
    }

    public void onCreate(long id, DataObject obj) {
        boolean available = obj.isNull("unavailable") || !obj.getBoolean("unavailable");
        log.trace("Received guild create for id: {} available: {}", id, available);

        if (available && unavailableGuilds.contains(id) && !setupNodes.containsKey(id)) {
            // Guild was unavailable for a moment, its back now so initialize it again!
            unavailableGuilds.remove(id);
            setupNodes.put(id, new GuildSetupNode(id, this, GuildSetupNode.Type.AVAILABLE));
        }

        GuildSetupNode node = setupNodes.get(id);
        if (node == null) {
            // this is a join event
            node = new GuildSetupNode(id, this, GuildSetupNode.Type.JOIN);
            setupNodes.put(id, node);
            // do not increment incomplete counter, it is only relevant to init guilds
        } else if (node.markedUnavailable && available && incompleteCount > 0) {
            //Looks like this guild decided to become available again during startup
            // that means we can now consider it for ReadyEvent status again!
            if (node.sync)
                syncingCount++;
            incompleteCount++;
        }
        node.handleCreate(obj);
    }

    public boolean isLocked(long id) {
        return setupNodes.containsKey(id);
    }

    public boolean isUnavailable(long id) {
        return unavailableGuilds.contains(id);
    }

    public void cacheEvent(long guildId, DataObject event) {
        GuildSetupNode node = setupNodes.get(guildId);
        if (node != null)
            node.cacheEvent(event);
        else
            log.warn("Attempted to cache event for a guild that is not locked. {}", event, new IllegalStateException());
    }

    public void clearCache() {
        setupNodes.clear();
        chunkingGuilds.clear();
        unavailableGuilds.clear();
        incompleteCount = 0;
        close();
        synchronized (pendingChunks) {
            pendingChunks.clear();
        }
    }

    public void close() {
        if (timeoutHandle != null)
            timeoutHandle.cancel(false);
    }

    // Chunking

    int getIncompleteCount() {
        return incompleteCount;
    }

    int getChunkingCount() {
        return chunkingGuilds.size();
    }

    void sendChunkRequest(Object obj) {
        log.debug("Sending chunking requests for {} guilds", obj instanceof DataArray ? ((DataArray) obj).length() : 1);

        long timeout = System.currentTimeMillis() + CHUNK_TIMEOUT;
        synchronized (pendingChunks) {
            if (obj instanceof DataArray) {
                DataArray arr = (DataArray) obj;
                for (Object o : arr)
                    pendingChunks.put((long) o, timeout);
            } else {
                pendingChunks.put((long) obj, timeout);
            }
        }

        getJDA().getClient().chunkOrSyncRequest(
                DataObject.empty()
                        .put("op", WebSocketCode.MEMBER_CHUNK_REQUEST)
                        .put("d", DataObject.empty()
                                .put("guild_id", obj)
                                .put("query", "")
                                .put("limit", 0)));
    }

    private void tryChunking() {
        if (chunkingGuilds.size() >= 50) {
            // request chunks
            final DataArray subset = DataArray.empty();
            for (final TLongIterator it = chunkingGuilds.iterator(); subset.length() < 50; ) {
                subset.add(it.next());
                it.remove();
            }
            sendChunkRequest(subset);
        }
        if (incompleteCount > 0 && chunkingGuilds.size() >= incompleteCount) {
            // request last chunks
            final DataArray array = DataArray.empty();
            chunkingGuilds.forEach((guild) -> {
                array.add(guild);
                return true;
            });
            chunkingGuilds.clear();
            sendChunkRequest(array);
        }
    }

    // Syncing

    private void sendSyncRequest(DataArray arr) {
        log.debug("Sending syncing requests for {} guilds", arr.length());

        getJDA().getClient().chunkOrSyncRequest(
                DataObject.empty()
                        .put("op", WebSocketCode.GUILD_SYNC)
                        .put("d", arr));
    }

    private void trySyncing() {
        if (syncingGuilds.size() >= 50) {
            // request chunks
            final DataArray subset = DataArray.empty();
            for (final TLongIterator it = syncingGuilds.iterator(); subset.length() < 50; ) {
                subset.add(it.next());
                it.remove();
            }
            sendSyncRequest(subset);
            syncingCount -= subset.length();
        }
        if (syncingCount > 0 && syncingGuilds.size() >= syncingCount) {
            final DataArray array = DataArray.empty();
            syncingGuilds.forEach((guild) -> {
                array.add(guild);
                return true;
            });
            syncingGuilds.clear();
            sendSyncRequest(array);
            syncingCount = 0;
        }
    }

    public enum Status {
        INIT,
        SYNCING,
        CHUNKING,
        BUILDING,
        READY,
        UNAVAILABLE
    }

    @FunctionalInterface
    public interface StatusListener {
        void onStatusChange(long guildId, Status oldStatus, Status newStatus);
    }

    private class ChunkTimeout implements Runnable {
        @Override
        public void run() {
            if (pendingChunks.isEmpty())
                return;
            synchronized (pendingChunks) {
                TLongLongIterator it = pendingChunks.iterator();
                List<DataArray> requests = new LinkedList<>();
                DataArray arr = DataArray.empty();
                while (it.hasNext()) {
                    // key=guild_id, value=timeout
                    it.advance();
                    if (System.currentTimeMillis() <= it.value())
                        continue;
                    arr.add(it.key());

                    if (arr.length() == 50) {
                        requests.add(arr);
                        arr = DataArray.empty();
                    }
                }
                if (arr.length() > 0)
                    requests.add(arr);
                requests.forEach(GuildSetupController.this::sendChunkRequest);
            }
        }
    }
}
