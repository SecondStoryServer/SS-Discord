package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.hooks.IEventManager;
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


    long getGatewayPing();


    @Nonnull
    default JDA awaitStatus(@Nonnull JDA.Status status) throws InterruptedException {
        //This is done to retain backwards compatible ABI as it would otherwise change the signature of the method
        // which would require recompilation for all users (including extension libraries)
        return awaitStatus(status, new JDA.Status[0]);
    }


    @Nonnull
    JDA awaitStatus(@Nonnull JDA.Status status, @Nonnull JDA.Status... failOn) throws InterruptedException;


    @Nonnull
    default JDA awaitReady() throws InterruptedException {
        return awaitStatus(Status.CONNECTED);
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


    void addEventListener(@Nonnull Object... listeners);


    @Nonnull
    SnowflakeCacheView<User> getUserCache();


    @Nullable
    default User getUserById(@Nonnull String id) {
        return getUserCache().getElementById(id);
    }


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


    @Nonnull
    default List<Role> getRoles() {
        return getRoleCache().asList();
    }


    @Nullable
    default Role getRoleById(long id) {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
        Checks.notNull(type, "ChannelType");
        switch (type) {
            case TEXT:
                return getTextChannelById(id);
            case STORE:
                return getStoreChannelById(id);
            case CATEGORY:
                return getCategoryById(id);
        }
        return null;
    }


    @Nonnull
    SnowflakeCacheView<Category> getCategoryCache();


    @Nullable
    default Category getCategoryById(long id) {
        return getCategoryCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<StoreChannel> getStoreChannelCache();


    @Nullable
    default StoreChannel getStoreChannelById(long id) {
        return getStoreChannelCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<TextChannel> getTextChannelCache();


    @Nullable
    default TextChannel getTextChannelById(long id) {
        return getTextChannelCache().getElementById(id);
    }

    @Nonnull
    SnowflakeCacheView<PrivateChannel> getPrivateChannelCache();


    @Nullable
    default PrivateChannel getPrivateChannelById(long id) {
        return getPrivateChannelCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();


    @Nullable
    default Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }


    @Nonnull
    IEventManager getEventManager();


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
