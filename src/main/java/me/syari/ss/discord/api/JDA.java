package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Emote;
import me.syari.ss.discord.internal.entities.Guild;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface JDA {

    enum Status {

        INITIALIZING(true),

        INITIALIZED(true),

        LOGGING_IN(true),

        CONNECTING_TO_WEBSOCKET(true),

        IDENTIFYING_SESSION(true),

        AWAITING_LOGIN_CONFIRMATION(true),

        LOADING_SUBSYSTEMS(true),

        CONNECTED(true),

        DISCONNECTED(false),

        RECONNECT_QUEUED(false),

        WAITING_TO_RECONNECT(false),

        ATTEMPTING_TO_RECONNECT(false),

        SHUTTING_DOWN(false),

        SHUTDOWN(false);

        private final boolean isInit;

        Status(boolean isInit) {
            this.isInit = isInit;
        }

        public boolean isInit() {
            return isInit;
        }
    }


    class ShardInfo {

        public static final ShardInfo SINGLE = new ShardInfo(0, 1);

        final int shardId;
        final int shardTotal;

        public ShardInfo(int shardId, int shardTotal) {
            this.shardId = shardId;
            this.shardTotal = shardTotal;
        }


        public int getShardId() {
            return shardId;
        }


        public int getShardTotal() {
            return shardTotal;
        }


        public String getShardString() {
            return "[" + shardId + " / " + shardTotal + "]";
        }

        @Override
        public String toString() {
            return "Shard " + getShardString();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ShardInfo))
                return false;

            ShardInfo oInfo = (ShardInfo) o;
            return shardId == oInfo.getShardId() && shardTotal == oInfo.getShardTotal();
        }
    }


    default void awaitStatus(@Nonnull Status status) throws InterruptedException {
        awaitStatus(status, new Status[0]);
    }


    void awaitStatus(@Nonnull Status status, @Nonnull Status... failOn) throws InterruptedException;


    default void awaitReady() throws InterruptedException {
        awaitStatus(Status.CONNECTED);
    }


    @Nonnull
    SnowflakeCacheView<User> getUserCache();


    @Nullable
    default User getUserById(long id) {
        return getUserCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Guild> getGuildCache();


    @Nonnull
    default List<Guild> getGuilds() {
        return getGuildCache().asList();
    }


    @Nullable
    default Guild getGuildById(long id) {
        return getGuildCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<TextChannel> getTextChannelCache();


    @Nullable
    default TextChannel getTextChannelById(long id) {
        return getTextChannelCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();


    @Nullable
    default Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }

    void shutdown();
}
