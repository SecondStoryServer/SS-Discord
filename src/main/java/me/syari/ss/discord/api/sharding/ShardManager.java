package me.syari.ss.discord.api.sharding;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.ShardCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;


public interface ShardManager {
    @Nullable
    default Guild getGuildById(final long id) {
        return getGuildCache().getElementById(id);
    }


    @Nullable
    default Guild getGuildById(@Nonnull final String id) {
        return getGuildById(MiscUtil.parseSnowflake(id));
    }


    @Nonnull
    default SnowflakeCacheView<Guild> getGuildCache() {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getGuildCache));
    }

    @Nonnull
    default List<Guild> getGuilds() {
        return this.getGuildCache().asList();
    }


    @Nullable
    default Role getRoleById(final long id) {
        return this.getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(@Nonnull final String id) {
        return this.getRoleCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<Role> getRoleCache() {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getRoleCache));
    }


    @Nonnull
    default List<Role> getRoles() {
        return this.getRoleCache().asList();
    }


    @Nonnull
    ShardCacheView getShardCache();


    @Nullable
    default TextChannel getTextChannelById(final long id) {
        return this.getTextChannelCache().getElementById(id);
    }


    @Nullable
    default TextChannel getTextChannelById(@Nonnull final String id) {
        return this.getTextChannelCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getTextChannelCache));
    }

    @Nonnull
    default SnowflakeCacheView<User> getUserCache() {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getUserCache));
    }


    @Nonnull
    default List<User> getUsers() {
        return this.getUserCache().asList();
    }

    @Deprecated
    @DeprecatedSince("4.0.0")
    @ReplaceWith("setActivity()")
    default void setGame(@Nullable final Activity game) {
        this.setActivityProvider(id -> game);
    }


    default void setActivity(@Nullable final Activity activity) {
        this.setActivityProvider(id -> activity);
    }


    default void setActivityProvider(@Nullable final IntFunction<? extends Activity> activityProvider) {
        this.getShardCache().forEach(jda -> jda.getPresence().setActivity(activityProvider == null ? null : activityProvider.apply(jda.getShardInfo().getShardId())));
    }


    default void setStatus(@Nullable final OnlineStatus status) {
        this.setStatusProvider(id -> status);
    }


    default void setStatusProvider(@Nullable final IntFunction<OnlineStatus> statusProvider) {
        this.getShardCache().forEach(jda -> jda.getPresence().setStatus(statusProvider == null ? null : statusProvider.apply(jda.getShardInfo().getShardId())));
    }


    void shutdown();


    void shutdown(int shardId);


    void start(int shardId);
}
