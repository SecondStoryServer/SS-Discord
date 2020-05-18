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
    private final long id;
    private final GuildSetupController controller;
    private final List<DataObject> cachedEvents = new LinkedList<>();
    private TLongObjectMap<DataObject> members;
    private TLongSet removedMembers;
    private DataObject partialGuild;
    private int expectedMemberCount = 1;
    boolean requestedChunk;

    final boolean sync;
    boolean firedUnavailableJoin = false;
    boolean markedUnavailable = false;
    GuildSetupController.Status status = GuildSetupController.Status.INIT;

    GuildSetupNode(long id, GuildSetupController controller) {
        this.id = id;
        this.controller = controller;
        this.sync = false;
    }

    public int getExpectedMemberCount() {
        return expectedMemberCount;
    }

    public int getCurrentMemberCount() {
        TLongHashSet knownMembers = new TLongHashSet(members.keySet());
        knownMembers.removeAll(removedMembers);
        return knownMembers.size();
    }

    @Override
    public String toString() {
        return "GuildSetupNode[" + id + "|" + status + ']' +
                '{' +
                "expectedMemberCount=" + expectedMemberCount + ", " +
                "requestedChunk=" + requestedChunk + ", " +
                "sync=" + sync + ", " +
                "markedUnavailable=" + markedUnavailable +
                '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GuildSetupNode))
            return false;
        GuildSetupNode node = (GuildSetupNode) obj;
        return node.id == id;
    }

    private GuildSetupController getController() {
        return controller;
    }

    void updateStatus(GuildSetupController.Status status) {
        if (status == this.status)
            return;
        try {
            getController().listener.onStatusChange(id, this.status, status);
        } catch (Exception ex) {
            GuildSetupController.log.error("Uncaught exception in status listener", ex);
        }
        this.status = status;
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
        if (unavailable) {
            if (!firedUnavailableJoin) {
                firedUnavailableJoin = true;
            }
            return;
        }
        ensureMembers();
    }

    boolean handleMemberChunk(DataArray arr) {
        if (partialGuild == null) {
            GuildSetupController.log.debug("Dropping member chunk due to unavailable guild");
            return true;
        }
        for (int index = 0; index < arr.length(); index++) {
            DataObject obj = arr.getObject(index);
            long id = obj.getObject("user").getLong("id");
            members.put(id, obj);
        }

        if (members.size() >= expectedMemberCount || !getController().getJDA().chunkGuild(id)) {
            completeSetup();
            return false;
        }
        return true;
    }

    void cacheEvent(@NotNull DataObject event) {
        GuildSetupController.log.trace("Caching {} event during init. GuildId: {}", event.getString("t"), id);
        cachedEvents.add(event);
        int cacheSize = cachedEvents.size();
        if (cacheSize >= 2000 && cacheSize % 1000 == 0) {
            GuildSetupController controller = getController();
            GuildSetupController.log.warn(
                    "Accumulating suspicious amounts of cached events during guild setup, " +
                            "something might be wrong. Cached: {} Members: {}/{} Status: {} GuildId: {} Incomplete: {}/{}",
                    cacheSize, getCurrentMemberCount(), getExpectedMemberCount(),
                    status, id, controller.getChunkingCount(), controller.getIncompleteCount());

            if (status == GuildSetupController.Status.CHUNKING) {
                GuildSetupController.log.debug("Forcing new chunk request for guild: {}", id);
                controller.sendChunkRequest(id);
            }
        }
    }

    private void completeSetup() {
        updateStatus(GuildSetupController.Status.BUILDING);
        JDA api = getController().getJDA();
        TLongIterator iterator = removedMembers.iterator();
        while (iterator.hasNext()) {
            members.remove(iterator.next());
        }
        removedMembers.clear();
        Guild guild = api.getEntityBuilder().createGuild(id, partialGuild, expectedMemberCount);
        if (requestedChunk) {
            getController().ready(id);
        } else {
            getController().remove(id);
        }
        updateStatus(GuildSetupController.Status.READY);
        GuildSetupController.log.debug("Finished setup for guild {} firing cached events {}", id, cachedEvents.size());
        api.getClient().handle(cachedEvents);
        api.getEventCache().playbackCache(EventCache.Type.GUILD, id);
        guild.acknowledgeMembers();
    }

    private void ensureMembers() {
        expectedMemberCount = partialGuild.getInt("member_count");
        members = new TLongObjectHashMap<>(expectedMemberCount);
        removedMembers = new TLongHashSet();
        DataArray memberArray = partialGuild.getArray("members");
        if (!getController().getJDA().chunkGuild(id)) {
            handleMemberChunk(memberArray);
        } else if (memberArray.length() < expectedMemberCount && !requestedChunk) {
            updateStatus(GuildSetupController.Status.CHUNKING);
            getController().addGuildForChunking(id);
            requestedChunk = true;
        } else if (handleMemberChunk(memberArray) && !requestedChunk) {
            GuildSetupController.log.trace(
                    "Received suspicious members with a guild payload. Attempting to chunk. " +
                            "member_count: {} members: {} actual_members: {} guild_id: {}",
                    expectedMemberCount, memberArray.length(), members.size(), id);
            members.clear();
            updateStatus(GuildSetupController.Status.CHUNKING);
            getController().addGuildForChunking(id);
            requestedChunk = true;
        }
    }
}
