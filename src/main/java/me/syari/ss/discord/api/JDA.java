package me.syari.ss.discord.api;

import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Emote;
import me.syari.ss.discord.internal.entities.Guild;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    void awaitStatus(@NotNull Status status, @NotNull Status... failOn) throws InterruptedException;

    default void awaitReady() throws InterruptedException {
        awaitStatus(Status.CONNECTED);
    }

    @NotNull
    SnowflakeCacheView<User> getUserCache();

    @Nullable
    default User getUserById(long id) {
        return getUserCache().getElementById(id);
    }

    @NotNull
    SnowflakeCacheView<Guild> getGuildCache();

    @NotNull
    default List<Guild> getGuilds() {
        return getGuildCache().asList();
    }

    @Nullable
    default Guild getGuildById(long id) {
        return getGuildCache().getElementById(id);
    }

    @NotNull
    SnowflakeCacheView<TextChannel> getTextChannelCache();

    @Nullable
    default TextChannel getTextChannelById(long id) {
        return getTextChannelCache().getElementById(id);
    }

    @NotNull
    SnowflakeCacheView<Emote> getEmoteCache();

    @Nullable
    default Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }

    void shutdown();
}
