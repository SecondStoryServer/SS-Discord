

package me.syari.ss.discord.api;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.managers.AudioManager;
import me.syari.ss.discord.api.managers.DirectAudioController;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.GuildAction;
import me.syari.ss.discord.api.sharding.ShardManager;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.requests.CompletedRestAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import okhttp3.OkHttpClient;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;


public interface JDA
{

    enum Status
    {

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

        Status(boolean isInit)
        {
            this.isInit = isInit;
        }

        Status()
        {
            this.isInit = false;
        }

        public boolean isInit()
        {
            return isInit;
        }
    }


    class ShardInfo
    {

        public static final ShardInfo SINGLE = new ShardInfo(0, 1);

        int shardId;
        int shardTotal;

        public ShardInfo(int shardId, int shardTotal)
        {
            this.shardId = shardId;
            this.shardTotal = shardTotal;
        }


        public int getShardId()
        {
            return shardId;
        }


        public int getShardTotal()
        {
            return shardTotal;
        }


        public String getShardString()
        {
            return "[" + shardId + " / " + shardTotal + "]";
        }

        @Override
        public String toString()
        {
            return "Shard " + getShardString();
        }

        @Override
        public boolean equals(Object o)
        {
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
    default RestAction<Long> getRestPing()
    {
        AtomicLong time = new AtomicLong();
        Route.CompiledRoute route = Route.Self.GET_SELF.compile();
        RestActionImpl<Long> action = new RestActionImpl<>(this, route, (response, request) -> System.currentTimeMillis() - time.get());
        action.setCheck(() -> {
            time.set(System.currentTimeMillis());
            return true;
        });
        return action;
    }


    @Nonnull
    default JDA awaitStatus(@Nonnull JDA.Status status) throws InterruptedException
    {
        //This is done to retain backwards compatible ABI as it would otherwise change the signature of the method
        // which would require recompilation for all users (including extension libraries)
        return awaitStatus(status, new JDA.Status[0]);
    }


    @Nonnull
    JDA awaitStatus(@Nonnull JDA.Status status, @Nonnull JDA.Status... failOn) throws InterruptedException;


    @Nonnull
    default JDA awaitReady() throws InterruptedException
    {
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


    @Nonnull
    DirectAudioController getDirectAudioController();


    void setEventManager(@Nullable IEventManager manager);


    void addEventListener(@Nonnull Object... listeners);


    void removeEventListener(@Nonnull Object... listeners);


    @Nonnull
    List<Object> getRegisteredListeners();


    @Nonnull
    @CheckReturnValue
    GuildAction createGuild(@Nonnull String name);


    @Nonnull
    CacheView<AudioManager> getAudioManagerCache();


    @Nonnull
    default List<AudioManager> getAudioManagers()
    {
        return getAudioManagerCache().asList();
    }



    @Nonnull
    SnowflakeCacheView<User> getUserCache();


    @Nonnull
    default List<User> getUsers()
    {
        return getUserCache().asList();
    }


    @Nullable
    default User getUserById(@Nonnull String id)
    {
        return getUserCache().getElementById(id);
    }


    @Nullable
    default User getUserById(long id)
    {
        return getUserCache().getElementById(id);
    }


    @Nullable
    default User getUserByTag(@Nonnull String tag)
    {
        Checks.notNull(tag, "Tag");
        Matcher matcher = User.USER_TAG.matcher(tag);
        Checks.check(matcher.matches(), "Invalid tag format!");
        String username = matcher.group(1);
        String discriminator = matcher.group(2);
        return getUserByTag(username, discriminator);
    }


    @Nullable
    default User getUserByTag(@Nonnull String username, @Nonnull String discriminator)
    {
        Checks.notNull(username, "Username");
        Checks.notNull(discriminator, "Discriminator");
        Checks.check(discriminator.length() == 4 && Helpers.isNumeric(discriminator), "Invalid format for discriminator!");
        Checks.check(username.length() >= 2 && username.length() <= 32, "Username must be between 2 and 32 characters in length!");
        return getUserCache().applyStream(stream ->
            stream.filter(it -> it.getDiscriminator().equals(discriminator))
                  .filter(it -> it.getName().equals(username))
                  .findFirst()
                  .orElse(null)
        );
    }


    @Nonnull
    default List<User> getUsersByName(@Nonnull String name, boolean ignoreCase)
    {
        return getUserCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    List<Guild> getMutualGuilds(@Nonnull User... users);


    @Nonnull
    List<Guild> getMutualGuilds(@Nonnull Collection<User> users);


    @Nonnull
    @CheckReturnValue
    RestAction<User> retrieveUserById(@Nonnull String id);


    @Nonnull
    @CheckReturnValue
    RestAction<User> retrieveUserById(long id);


    @Nonnull
    SnowflakeCacheView<Guild> getGuildCache();


    @Nonnull
    default List<Guild> getGuilds()
    {
        return getGuildCache().asList();
    }


    @Nullable
    default Guild getGuildById(@Nonnull String id)
    {
        return getGuildCache().getElementById(id);
    }


    @Nullable
    default Guild getGuildById(long id)
    {
        return getGuildCache().getElementById(id);
    }


    @Nonnull
    default List<Guild> getGuildsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getGuildCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    Set<String> getUnavailableGuilds();


    boolean isUnavailable(long guildId);


    @Nonnull
    SnowflakeCacheView<Role> getRoleCache();


    @Nonnull
    default List<Role> getRoles()
    {
        return getRoleCache().asList();
    }


    @Nullable
    default Role getRoleById(@Nonnull String id)
    {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(long id)
    {
        return getRoleCache().getElementById(id);
    }


    @Nonnull
    default List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getRoleCache().getElementsByName(name, ignoreCase);
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull String id)
    {
        return getGuildChannelById(MiscUtil.parseSnowflake(id));
    }


    @Nullable
    default GuildChannel getGuildChannelById(long id)
    {
        GuildChannel channel = getTextChannelById(id);
        if (channel == null)
            channel = getVoiceChannelById(id);
        if (channel == null)
            channel = getStoreChannelById(id);
        if (channel == null)
            channel = getCategoryById(id);
        return channel;
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, @Nonnull String id)
    {
        return getGuildChannelById(type, MiscUtil.parseSnowflake(id));
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id)
    {
        Checks.notNull(type, "ChannelType");
        switch (type)
        {
            case TEXT:
                return getTextChannelById(id);
            case VOICE:
                return getVoiceChannelById(id);
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
    default Category getCategoryById(@Nonnull String id)
    {
        return getCategoryCache().getElementById(id);
    }


    @Nullable
    default Category getCategoryById(long id)
    {
        return getCategoryCache().getElementById(id);
    }


    @Nonnull
    default List<Category> getCategories()
    {
        return getCategoryCache().asList();
    }


    @Nonnull
    default List<Category> getCategoriesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getCategoryCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    SnowflakeCacheView<StoreChannel> getStoreChannelCache();


    @Nullable
    default StoreChannel getStoreChannelById(@Nonnull String id)
    {
        return getStoreChannelCache().getElementById(id);
    }


    @Nullable
    default StoreChannel getStoreChannelById(long id)
    {
        return getStoreChannelCache().getElementById(id);
    }


    @Nonnull
    default List<StoreChannel> getStoreChannels()
    {
        return getStoreChannelCache().asList();
    }


    @Nonnull
    default List<StoreChannel> getStoreChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getStoreChannelCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    SnowflakeCacheView<TextChannel> getTextChannelCache();


    @Nonnull
    default List<TextChannel> getTextChannels()
    {
        return getTextChannelCache().asList();
    }


    @Nullable
    default TextChannel getTextChannelById(@Nonnull String id)
    {
        return getTextChannelCache().getElementById(id);
    }


    @Nullable
    default TextChannel getTextChannelById(long id)
    {
        return getTextChannelCache().getElementById(id);
    }


    @Nonnull
    default List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getTextChannelCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    SnowflakeCacheView<VoiceChannel> getVoiceChannelCache();


    @Nonnull
    default List<VoiceChannel> getVoiceChannels()
    {
        return getVoiceChannelCache().asList();
    }


    @Nullable
    default VoiceChannel getVoiceChannelById(@Nonnull String id)
    {
        return getVoiceChannelCache().getElementById(id);
    }


    @Nullable
    default VoiceChannel getVoiceChannelById(long id)
    {
        return getVoiceChannelCache().getElementById(id);
    }


    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("jda.getVoiceChannelsByName(name, ignoreCase)")
    default List<VoiceChannel> getVoiceChannelByName(@Nonnull String name, boolean ignoreCase)
    {
        return getVoiceChannelsByName(name, ignoreCase);
    }


    @Nonnull
    default List<VoiceChannel> getVoiceChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getVoiceChannelCache().getElementsByName(name, ignoreCase);
    }


    @Nonnull
    SnowflakeCacheView<PrivateChannel> getPrivateChannelCache();


    @Nonnull
    default List<PrivateChannel> getPrivateChannels()
    {
        return getPrivateChannelCache().asList();
    }


    @Nullable
    default PrivateChannel getPrivateChannelById(@Nonnull String id)
    {
        return getPrivateChannelCache().getElementById(id);
    }


    @Nullable
    default PrivateChannel getPrivateChannelById(long id)
    {
        return getPrivateChannelCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();


    @Nonnull
    default List<Emote> getEmotes()
    {
        return getEmoteCache().asList();
    }


    @Nullable
    default Emote getEmoteById(@Nonnull String id)
    {
        return getEmoteCache().getElementById(id);
    }


    @Nullable
    default Emote getEmoteById(long id)
    {
        return getEmoteCache().getElementById(id);
    }


    @Nonnull
    default List<Emote> getEmotesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getEmoteCache().getElementsByName(name, ignoreCase);
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


    void setAutoReconnect(boolean reconnect);


    void setRequestTimeoutRetry(boolean retryOnTimeout);


    boolean isAutoReconnect();


    boolean isBulkDeleteSplittingEnabled();


    void shutdown();


    void shutdownNow();

    //
    // * Installs an auxiliary cable into the given port of your system.
    // *
    // * @param  port
    // *         The port in which the cable should be installed.
    // *
    // * @return {@link me.syari.ss.discord.api.requests.restaction.AuditableRestAction AuditableRestAction}{@literal <}{@link Void}{@literal >}
    // */
    //AuditableRestAction<Void> installAuxiliaryCable(int port);


    @Nonnull
    AccountType getAccountType();


    @Nonnull
    @CheckReturnValue
    RestAction<ApplicationInfo> retrieveApplicationInfo();


    @Nonnull
    String getInviteUrl(@Nullable Permission... permissions);


    @Nonnull
    String getInviteUrl(@Nullable Collection<Permission> permissions);


    @Nullable
    ShardManager getShardManager();


    @Nonnull
    @CheckReturnValue
    RestAction<Webhook> retrieveWebhookById(@Nonnull String webhookId);


    @Nonnull
    @CheckReturnValue
    default RestAction<Webhook> retrieveWebhookById(long webhookId)
    {
        return retrieveWebhookById(Long.toUnsignedString(webhookId));
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Integer> installAuxiliaryPort()
    {
        int port = ThreadLocalRandom.current().nextInt();
        if (Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
            }
            catch (IOException | URISyntaxException e)
            {
                throw  new IllegalStateException("No port available");
            }
        }
        else throw new IllegalStateException("No port available");
        return new CompletedRestAction<>(this, port);
    }
}
