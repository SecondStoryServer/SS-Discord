package me.syari.ss.discord.internal.handle;

import gnu.trove.iterator.TLongIterator;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class GuildSetupController {
    protected static final int CHUNK_TIMEOUT = 10000;
    protected static final Logger log = JDALogger.getLog(GuildSetupController.class);

    private final JDAImpl api;
    private final TLongObjectMap<GuildSetupNode> setupNodes = new TLongObjectHashMap<>();
    private final TLongSet chunkingGuilds = new TLongHashSet();
    private final TLongLongMap pendingChunks = new TLongLongHashMap();
    private final TLongSet unavailableGuilds = new TLongHashSet();

    private int incompleteCount = 0;

    protected final StatusListener listener = (id, oldStatus, newStatus) -> log.trace("[{}] Updated status {}->{}", id, oldStatus, newStatus);

    public GuildSetupController(JDAImpl api) {
        this.api = api;
    }

    JDAImpl getJDA() {
        return api;
    }

    void addGuildForChunking(long id, boolean join) {
        log.trace("Adding guild for chunking ID: {}", id);
        if (join || incompleteCount <= 0) {
            if (incompleteCount <= 0) {
                sendChunkRequest(id);
                return;
            }
            incompleteCount++;
        }
        chunkingGuilds.add(id);
        tryChunking();
    }

    void remove(long id) {
        unavailableGuilds.remove(id);
        setupNodes.remove(id);
        chunkingGuilds.remove(id);
        synchronized (pendingChunks) {
            pendingChunks.remove(id);
        }
    }

    public void ready(long id) {
        remove(id);
        WebSocketClient client = getJDA().getClient();
        if (--incompleteCount < 1 && !client.isReady())
            client.ready();
        else
            tryChunking();
    }

    public void onCreate(long id, @NotNull DataObject obj) {
        boolean available = obj.isNull("unavailable") || !obj.getBoolean("unavailable");
        log.trace("Received guild create for id: {} available: {}", id, available);

        if (available && unavailableGuilds.contains(id) && !setupNodes.containsKey(id)) {
            unavailableGuilds.remove(id);
            setupNodes.put(id, new GuildSetupNode(id, this, GuildSetupNode.Type.AVAILABLE));
        }

        GuildSetupNode node = setupNodes.get(id);
        if (node == null) {
            node = new GuildSetupNode(id, this, GuildSetupNode.Type.JOIN);
            setupNodes.put(id, node);
        } else if (node.markedUnavailable && available && incompleteCount > 0) {
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
        synchronized (pendingChunks) {
            pendingChunks.clear();
        }
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
            final DataArray subset = DataArray.empty();
            for (final TLongIterator it = chunkingGuilds.iterator(); subset.length() < 50; ) {
                subset.add(it.next());
                it.remove();
            }
            sendChunkRequest(subset);
        }
        if (incompleteCount > 0 && chunkingGuilds.size() >= incompleteCount) {
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

    public enum Status {
        INIT,
        CHUNKING,
        BUILDING,
        READY
    }

    @FunctionalInterface
    public interface StatusListener {
        void onStatusChange(long guildId, Status oldStatus, Status newStatus);
    }

}
