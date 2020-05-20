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
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import me.syari.ss.discord.internal.requests.WebSocketCode;
import org.jetbrains.annotations.NotNull;

public class GuildSetupController {
    protected static final int CHUNK_TIMEOUT = 10000;

    private final JDA api;
    private final TLongObjectMap<GuildSetupNode> setupNodes = new TLongObjectHashMap<>();
    private final TLongSet chunkingGuilds = new TLongHashSet();
    private final TLongLongMap pendingChunks = new TLongLongHashMap();
    private final TLongSet unavailableGuilds = new TLongHashSet();
    private int incompleteCount = 0;

    public GuildSetupController(JDA api) {
        this.api = api;
    }

    protected JDA getJDA() {
        return api;
    }

    void addGuildForChunking(long id) {
        if (incompleteCount <= 0) {
            sendChunkRequest(id);
        } else {
            incompleteCount++;
            chunkingGuilds.add(id);
            tryChunking();
        }
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
        WebSocketClient client = api.getClient();
        incompleteCount--;
        if (incompleteCount < 1 && !client.isReady()) {
            client.ready();
        } else {
            tryChunking();
        }
    }

    public void onCreate(long id, @NotNull DataObject obj) {
        GuildSetupNode node = setupNodes.get(id);
        if (node == null) {
            node = new GuildSetupNode(id, this);
            setupNodes.put(id, node);
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
        if (node != null) node.cacheEvent(event);
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

    void sendChunkRequest(Object object) {
        long timeout = System.currentTimeMillis() + CHUNK_TIMEOUT;
        synchronized (pendingChunks) {
            if (object instanceof DataArray) {
                DataArray array = (DataArray) object;
                for (Object o : array) {
                    pendingChunks.put((long) o, timeout);
                }
            } else {
                pendingChunks.put((long) object, timeout);
            }
        }

        api.getClient().chunkOrSyncRequest(
                DataObject.empty()
                        .put("op", WebSocketCode.MEMBER_CHUNK_REQUEST)
                        .put("d", DataObject.empty()
                                .put("guild_id", object)
                                .put("query", "")
                                .put("limit", 0)
                        )
        );
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

    public enum Status {
        INIT,
        CHUNKING,
        BUILDING,
        READY
    }
}
