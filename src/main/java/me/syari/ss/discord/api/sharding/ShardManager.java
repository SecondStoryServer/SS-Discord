
package me.syari.ss.discord.api.sharding;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.hooks.EventListener;
import me.syari.ss.discord.api.hooks.InterfacedEventManager;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDA.Status;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.ShardCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.CompletedRestAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


public interface ShardManager
{

    default void addEventListener(@Nonnull final Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");
        this.getShardCache().forEach(jda -> jda.addEventListener(listeners));
    }


    default void removeEventListener(@Nonnull final Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");
        this.getShardCache().forEach(jda -> jda.removeEventListener(listeners));
    }


    default void addEventListeners(@Nonnull final IntFunction<Object> eventListenerProvider)
    {
        Checks.notNull(eventListenerProvider, "event listener provider");
        this.getShardCache().forEach(jda ->
        {
            Object listener = eventListenerProvider.apply(jda.getShardInfo().getShardId());
            if (listener != null) jda.addEventListener(listener);
        });
    }


    default void removeEventListeners(@Nonnull final IntFunction<Collection<Object>> eventListenerProvider)
    {
        Checks.notNull(eventListenerProvider, "event listener provider");
        this.getShardCache().forEach(jda ->
            jda.removeEventListener(eventListenerProvider.apply(jda.getShardInfo().getShardId()))
        );
    }


    default void removeEventListenerProvider(@Nonnull IntFunction<Object> eventListenerProvider)
    {
    }


    int getShardsQueued();


    default int getShardsRunning()
    {
        return (int) this.getShardCache().size();
    }


    default int getShardsTotal()
    {
        return this.getShardsQueued() + this.getShardsRunning();
    }


    @Nonnull
    default RestAction<ApplicationInfo> retrieveApplicationInfo()
    {
        return this.getShardCache().stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no active shards"))
                .retrieveApplicationInfo();
    }


    default double getAverageGatewayPing()
    {
        return this.getShardCache()
                .stream()
                .mapToLong(JDA::getGatewayPing)
                .filter(ping -> ping != -1)
                .average()
                .orElse(-1D);
    }


    @Nonnull
    default List<Category> getCategories()
    {
        return this.getCategoryCache().asList();
    }


    @Nonnull
    default List<Category> getCategoriesByName(@Nonnull final String name, final boolean ignoreCase)
    {
        return this.getCategoryCache().getElementsByName(name, ignoreCase);
    }


    @Nullable
    default Category getCategoryById(final long id)
    {
        return this.getCategoryCache().getElementById(id);
    }


    @Nullable
    default Category getCategoryById(@Nonnull final String id)
    {
        return this.getCategoryCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<Category> getCategoryCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getCategoryCache));
    }


    @Nullable
    default Emote getEmoteById(final long id)
    {
        return this.getEmoteCache().getElementById(id);
    }


    @Nullable
    default Emote getEmoteById(@Nonnull final String id)
    {
        return this.getEmoteCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<Emote> getEmoteCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getEmoteCache));
    }


    @Nonnull
    default List<Emote> getEmotes()
    {
        return this.getEmoteCache().asList();
    }


    @Nonnull
    default List<Emote> getEmotesByName(@Nonnull final String name, final boolean ignoreCase)
    {
        return this.getEmoteCache().getElementsByName(name, ignoreCase);
    }


    @Nullable
    default Guild getGuildById(final long id)
    {
        return getGuildCache().getElementById(id);
    }


    @Nullable
    default Guild getGuildById(@Nonnull final String id)
    {
        return getGuildById(MiscUtil.parseSnowflake(id));
    }


    @Nonnull
    default List<Guild> getGuildsByName(@Nonnull final String name, final boolean ignoreCase)
    {
        return this.getGuildCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    default SnowflakeCacheView<Guild> getGuildCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getGuildCache));
    }


    @Nonnull
    default List<Guild> getGuilds()
    {
        return this.getGuildCache().asList();
    }


    @Nonnull
    default List<Guild> getMutualGuilds(@Nonnull final Collection<User> users)
    {
        Checks.noneNull(users, "users");
        return Collections.unmodifiableList(
                this.getGuildCache().stream()
                .filter(guild -> users.stream()
                        .allMatch(guild::isMember))
                .collect(Collectors.toList()));
    }


    @Nonnull
    default List<Guild> getMutualGuilds(@Nonnull final User... users)
    {
        Checks.notNull(users, "users");
        return this.getMutualGuilds(Arrays.asList(users));
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<User> retrieveUserById(@Nonnull String id)
    {
        return retrieveUserById(MiscUtil.parseSnowflake(id));
    }


    @Nonnull
    @CheckReturnValue
    default RestAction<User> retrieveUserById(long id)
    {
        JDA api = null;
        for (JDA shard : getShardCache())
        {
            api = shard;
            User user = shard.getUserById(id);
            if (user != null)
                return new CompletedRestAction<>(shard, user);
        }

        if (api == null)
            throw new IllegalStateException("no shards active");

        JDAImpl jda = (JDAImpl) api;
        Route.CompiledRoute route = Route.Users.GET_USER.compile(Long.toUnsignedString(id));
        return new RestActionImpl<>(jda, route, (response, request) -> jda.getEntityBuilder().createFakeUser(response.getObject(), false));
    }


    @Nullable
    default User getUserByTag(@Nonnull String tag)
    {
        return getShardCache().applyStream(stream ->
            stream.map(jda -> jda.getUserByTag(tag))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .orElse(null)
        );
    }


    @Nullable
    default User getUserByTag(@Nonnull String username, @Nonnull String discriminator)
    {
        return getShardCache().applyStream(stream ->
            stream.map(jda -> jda.getUserByTag(username, discriminator))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .orElse(null)
        );
    }


    @Nullable
    default PrivateChannel getPrivateChannelById(final long id)
    {
        return this.getPrivateChannelCache().getElementById(id);
    }


    @Nullable
    default PrivateChannel getPrivateChannelById(@Nonnull final String id)
    {
        return this.getPrivateChannelCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<PrivateChannel> getPrivateChannelCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getPrivateChannelCache));
    }


    @Nonnull
    default List<PrivateChannel> getPrivateChannels()
    {
        return this.getPrivateChannelCache().asList();
    }


    @Nullable
    default Role getRoleById(final long id)
    {
        return this.getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(@Nonnull final String id)
    {
        return this.getRoleCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<Role> getRoleCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getRoleCache));
    }


    @Nonnull
    default List<Role> getRoles()
    {
        return this.getRoleCache().asList();
    }


    @Nonnull
    default List<Role> getRolesByName(@Nonnull final String name, final boolean ignoreCase)
    {
        return this.getRoleCache().getElementsByName(name, ignoreCase);
    }


    @Nullable
    default JDA getShardById(final int id)
    {
        return this.getShardCache().getElementById(id);
    }


    @Nullable
    default JDA getShardById(@Nonnull final String id)
    {
        return this.getShardCache().getElementById(id);
    }


    @Nonnull
    ShardCacheView getShardCache();


    @Nonnull
    default List<JDA> getShards()
    {
        return this.getShardCache().asList();
    }


    @Nullable
    default JDA.Status getStatus(final int shardId)
    {
        final JDA jda = this.getShardCache().getElementById(shardId);
        return jda == null ? null : jda.getStatus();
    }


    @Nonnull
    default Map<JDA, Status> getStatuses()
    {
        return Collections.unmodifiableMap(this.getShardCache().stream()
                .collect(Collectors.toMap(Function.identity(), JDA::getStatus)));
    }


    @Nullable
    default TextChannel getTextChannelById(final long id)
    {
        return this.getTextChannelCache().getElementById(id);
    }


    @Nullable
    default TextChannel getTextChannelById(@Nonnull final String id)
    {
        return this.getTextChannelCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<TextChannel> getTextChannelCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getTextChannelCache));
    }


    @Nonnull
    default List<TextChannel> getTextChannels()
    {
        return this.getTextChannelCache().asList();
    }


    @Nullable
    default StoreChannel getStoreChannelById(final long id)
    {
        return this.getStoreChannelCache().getElementById(id);
    }


    @Nullable
    default StoreChannel getStoreChannelById(@Nonnull final String id)
    {
        return this.getStoreChannelCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<StoreChannel> getStoreChannelCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getStoreChannelCache));
    }


    @Nonnull
    default List<StoreChannel> getStoreChannels()
    {
        return this.getStoreChannelCache().asList();
    }
    

    @Nullable
    default User getUserById(final long id)
    {
        return this.getUserCache().getElementById(id);
    }


    @Nullable
    default User getUserById(@Nonnull final String id)
    {
        return this.getUserCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<User> getUserCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getUserCache));
    }


    @Nonnull
    default List<User> getUsers()
    {
        return this.getUserCache().asList();
    }


    @Nullable
    default VoiceChannel getVoiceChannelById(final long id)
    {
        return this.getVoiceChannelCache().getElementById(id);
    }


    @Nullable
    default VoiceChannel getVoiceChannelById(@Nonnull final String id)
    {
        return this.getVoiceChannelCache().getElementById(id);
    }


    @Nonnull
    default SnowflakeCacheView<VoiceChannel> getVoiceChannelCache()
    {
        return CacheView.allSnowflakes(() -> this.getShardCache().stream().map(JDA::getVoiceChannelCache));
    }


    @Nonnull
    default List<VoiceChannel> getVoiceChannels()
    {
        return this.getVoiceChannelCache().asList();
    }


    void restart();


    void restart(int id);


    @Deprecated
    @DeprecatedSince("4.0.0")
    @ReplaceWith("setActivity()")
    default void setGame(@Nullable final Activity game)
    {
        this.setActivityProvider(id -> game);
    }



    default void setActivity(@Nullable final Activity activity)
    {
        this.setActivityProvider(id -> activity);
    }


    default void setActivityProvider(@Nullable final IntFunction<? extends Activity> activityProvider)
    {
        this.getShardCache().forEach(jda -> jda.getPresence().setActivity(activityProvider == null ? null : activityProvider.apply(jda.getShardInfo().getShardId())));
    }


    default void setIdle(final boolean idle)
    {
        this.setIdleProvider(id -> idle);
    }


    default void setIdleProvider(@Nonnull final IntFunction<Boolean> idleProvider)
    {
        this.getShardCache().forEach(jda -> jda.getPresence().setIdle(idleProvider.apply(jda.getShardInfo().getShardId())));
    }


    default void setPresence(@Nullable final OnlineStatus status, @Nullable final Activity activity)
    {
        this.setPresenceProvider(id -> status, id -> activity);
    }


    default void setPresenceProvider(@Nullable final IntFunction<OnlineStatus> statusProvider, @Nullable final IntFunction<? extends Activity> activityProvider)
    {
        this.getShardCache().forEach(jda -> jda.getPresence().setPresence(statusProvider == null ? null : statusProvider.apply(jda.getShardInfo().getShardId()), activityProvider == null ? null : activityProvider.apply(jda.getShardInfo().getShardId())));
    }


    default void setStatus(@Nullable final OnlineStatus status)
    {
        this.setStatusProvider(id -> status);
    }


    default void setStatusProvider(@Nullable final IntFunction<OnlineStatus> statusProvider)
    {
        this.getShardCache().forEach(jda -> jda.getPresence().setStatus(statusProvider == null ? null : statusProvider.apply(jda.getShardInfo().getShardId())));
    }


    void shutdown();


    void shutdown(int shardId);


    void start(int shardId);
}
