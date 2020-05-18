package me.syari.ss.discord.internal.handle;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import me.syari.ss.discord.internal.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GuildSetupNode {
    private final List<DataObject> cachedEvents = new LinkedList<>();
    private TLongObjectMap<DataObject> members;
    private TLongSet removedMembers;
    private DataObject partialGuild;
    private int expectedMemberCount = 1;
    boolean requestedChunk;
    boolean markedUnavailable = false;
    GuildSetupController.Status status = GuildSetupController.Status.INIT;
    private final long id;
    private final GuildSetupController controller;
    private final JDA api;

    GuildSetupNode(long id, @NotNull GuildSetupController controller) {
        this.id = id;
        this.controller = controller;
        api = controller.getJDA();
    }

    @Override
    public String toString() {
        return "GuildSetupNode[" + id + "|" + status + ']' +
                '{' +
                "expectedMemberCount=" + expectedMemberCount + ", " +
                "requestedChunk=" + requestedChunk + ", " +
                "markedUnavailable=" + markedUnavailable +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GuildSetupNode)) return false;
        GuildSetupNode node = (GuildSetupNode) obj;
        return node.id == id;
    }

    void updateStatus(GuildSetupController.Status status) {
        if (status != this.status) this.status = status;
    }

    void handleCreate(DataObject obj) {
        if (partialGuild == null) {
            partialGuild = obj;
        } else {
            for (String key : obj.keys()) {
                partialGuild.put(key, obj.opt(key).orElse(null));
            }
        }
        boolean unavailable = partialGuild.getBoolean("unavailable");
        this.markedUnavailable = unavailable;
        if (unavailable) return;
        ensureMembers();
    }

    boolean handleMemberChunk(DataArray arr) {
        if (partialGuild == null) return true;
        for (int index = 0; index < arr.length(); index++) {
            DataObject obj = arr.getObject(index);
            long id = obj.getObject("user").getLong("id");
            members.put(id, obj);
        }
        if (expectedMemberCount <= members.size() || !api.chunkGuild(id)) {
            completeSetup();
            return false;
        }
        return true;
    }

    void cacheEvent(@NotNull DataObject event) {
        cachedEvents.add(event);
        int cacheSize = cachedEvents.size();
        if (2000 <= cacheSize && cacheSize % 1000 == 0 && status == GuildSetupController.Status.CHUNKING) {
            controller.sendChunkRequest(id);
        }
    }

    private void completeSetup() {
        updateStatus(GuildSetupController.Status.BUILDING);
        TLongIterator iterator = removedMembers.iterator();
        while (iterator.hasNext()) {
            members.remove(iterator.next());
        }
        removedMembers.clear();
        Guild guild = api.getEntityBuilder().createGuild(id, partialGuild, expectedMemberCount);
        if (requestedChunk) {
            controller.ready(id);
        } else {
            controller.remove(id);
        }
        updateStatus(GuildSetupController.Status.READY);
        api.getClient().handle(cachedEvents);
        api.getEventCache().playbackCache(EventCache.Type.GUILD, id);
        guild.acknowledgeMembers();
    }

    private void ensureMembers() {
        expectedMemberCount = partialGuild.getInt("member_count");
        members = new TLongObjectHashMap<>(expectedMemberCount);
        removedMembers = new TLongHashSet();
        DataArray memberArray = partialGuild.getArray("members");
        if (!api.chunkGuild(id)) {
            handleMemberChunk(memberArray);
        } else if (memberArray.length() < expectedMemberCount && !requestedChunk) {
            updateStatus(GuildSetupController.Status.CHUNKING);
            controller.addGuildForChunking(id);
            requestedChunk = true;
        } else if (handleMemberChunk(memberArray) && !requestedChunk) {
            members.clear();
            updateStatus(GuildSetupController.Status.CHUNKING);
            controller.addGuildForChunking(id);
            requestedChunk = true;
        }
    }
}
