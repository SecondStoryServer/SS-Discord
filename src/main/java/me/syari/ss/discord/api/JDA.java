package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.ListenerAdapter;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.utils.Checks;
import okhttp3.OkHttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;


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

        DISCONNECTED,

        RECONNECT_QUEUED,

        WAITING_TO_RECONNECT,

        ATTEMPTING_TO_RECONNECT,

        SHUTTING_DOWN,

        SHUTDOWN,

        FAILED_TO_LOGIN;

        private final boolean isInit;

        Status(boolean isInit) {
            this.isInit = isInit;
        }

        Status() {
            this.isInit = false;
        }

        public boolean isInit() {
            return isInit;
        }
    }


    class ShardInfo {

        public static final ShardInfo SINGLE = new ShardInfo(0, 1);

        int shardId;
        int shardTotal;

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


    @Nonnull
    Status getStatus();


    default void awaitStatus(@Nonnull Status status) throws InterruptedException {
        awaitStatus(status, new Status[0]);
    }


    void awaitStatus(@Nonnull Status status, @Nonnull Status... failOn) throws InterruptedException;


    default void awaitReady() throws InterruptedException {
        awaitStatus(Status.CONNECTED);
    }


    @Nonnull
    ScheduledExecutorService getRateLimitPool();


    @Nonnull
    ScheduledExecutorService getGatewayPool();


    @Nonnull
    ExecutorService getCallbackPool();


    @Nonnull
    OkHttpClient getHttpClient();


    void setEventManager(@Nullable IEventManager manager);


    void addEventListener(@Nonnull ListenerAdapter listeners);


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


    boolean isUnavailable(long guildId);


    @Nonnull
    SnowflakeCacheView<Role> getRoleCache();


    @Nullable
    default Role getRoleById(long id) {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
        Checks.notNull(type, "ChannelType");
        if (type == ChannelType.TEXT) {
            return getTextChannelById(id);
        }
        return null;
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


    @Nonnull
    SelfUser getSelfUser();


    @Nonnull
    Presence getPresence();


    @Nonnull
    ShardInfo getShardInfo();


    @Nonnull
    String getToken();


    long getResponseTotal();


    int getMaxReconnectDelay();


    boolean isAutoReconnect();


    void shutdown();


    void shutdownNow();
}
