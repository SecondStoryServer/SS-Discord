

package me.syari.ss.discord.api;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.events.GatewayPingEvent;
import me.syari.ss.discord.api.events.guild.GuildAvailableEvent;
import me.syari.ss.discord.api.events.guild.GuildJoinEvent;
import me.syari.ss.discord.api.events.guild.GuildUnavailableEvent;
import me.syari.ss.discord.api.events.message.MessageBulkDeleteEvent;
import me.syari.ss.discord.api.events.message.MessageDeleteEvent;
import me.syari.ss.discord.api.exceptions.AccountTypeException;
import me.syari.ss.discord.api.hooks.AnnotatedEventManager;
import me.syari.ss.discord.api.hooks.EventListener;
import me.syari.ss.discord.api.hooks.InterfacedEventManager;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.GuildAction;
import me.syari.ss.discord.api.sharding.ShardManager;
import me.syari.ss.discord.api.utils.SessionController;
import me.syari.ss.discord.api.utils.cache.CacheFlag;
import me.syari.ss.discord.internal.requests.CompletedRestAction;
import me.syari.ss.discord.internal.requests.RestActionImpl;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;
import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.managers.AudioManager;
import me.syari.ss.discord.api.managers.DirectAudioController;
import me.syari.ss.discord.api.managers.Presence;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.CacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;

/**
 * The core of JDA. Acts as a registry system of JDA. All parts of the the API can be accessed starting from this class.
 *
 * @see JDABuilder
 */
public interface JDA
{
    /**
     * Represents the connection status of JDA and its Main WebSocket.
     */
    enum Status
    {
        /**JDA is currently setting up supporting systems like the AudioSystem.*/
        INITIALIZING(true),
        /**JDA has finished setting up supporting systems and is ready to log in.*/
        INITIALIZED(true),
        /**JDA is currently attempting to log in.*/
        LOGGING_IN(true),
        /**JDA is currently attempting to connect it's websocket to Discord.*/
        CONNECTING_TO_WEBSOCKET(true),
        /**JDA has successfully connected it's websocket to Discord and is sending authentication*/
        IDENTIFYING_SESSION(true),
        /**JDA has sent authentication to discord and is awaiting confirmation*/
        AWAITING_LOGIN_CONFIRMATION(true),
        /**JDA is populating internal objects.
         * This process often takes the longest of all Statuses (besides CONNECTED)*/
        LOADING_SUBSYSTEMS(true),
        /**JDA has finished loading everything, is receiving information from Discord and is firing events.*/
        CONNECTED(true),
        /**JDA's main websocket has been disconnected. This <b>DOES NOT</b> mean JDA has shutdown permanently.
         * This is an in-between status. Most likely ATTEMPTING_TO_RECONNECT or SHUTTING_DOWN/SHUTDOWN will soon follow.*/
        DISCONNECTED,
        /** JDA session has been added to {@link SessionController SessionController}
         * and is awaiting to be dequeued for reconnecting.*/
        RECONNECT_QUEUED,
        /**When trying to reconnect to Discord JDA encountered an issue, most likely related to a lack of internet connection,
         * and is waiting to try reconnecting again.*/
        WAITING_TO_RECONNECT,
        /**JDA has been disconnected from Discord and is currently trying to reestablish the connection.*/
        ATTEMPTING_TO_RECONNECT,
        /**JDA has received a shutdown request or has been disconnected from Discord and reconnect is disabled, thus,
         * JDA is in the process of shutting down*/
        SHUTTING_DOWN,
        /**JDA has finished shutting down and this instance can no longer be used to communicate with the Discord servers.*/
        SHUTDOWN,
        /**While attempting to authenticate, Discord reported that the provided authentication information was invalid.*/
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

    /**
     * Represents the information used to create this shard.
     */
    class ShardInfo
    {
        /** Default sharding config with one shard */
        public static final ShardInfo SINGLE = new ShardInfo(0, 1);

        int shardId;
        int shardTotal;

        public ShardInfo(int shardId, int shardTotal)
        {
            this.shardId = shardId;
            this.shardTotal = shardTotal;
        }

        /**
         * Represents the id of the shard of the current instance.
         * <br>This value will be between 0 and ({@link #getShardTotal()} - 1).
         *
         * @return The id of the currently logged in shard.
         */
        public int getShardId()
        {
            return shardId;
        }

        /**
         * The total amount of shards based on the value provided during JDA instance creation using
         * {@link JDABuilder#useSharding(int, int)}.
         * <br>This <b>does not</b> query Discord to determine the total number of shards.
         * <br>This <b>does not</b> represent the amount of logged in shards.
         * <br>It strictly represents the integer value provided to discord
         * representing the total amount of shards that the developer indicated that it was going to use when
         * initially starting JDA.
         *
         * @return The total of shards based on the total provided by the developer during JDA initialization.
         */
        public int getShardTotal()
        {
            return shardTotal;
        }

        /**
         * Provides a shortcut method for easily printing shard info.
         * <br>Format: "[# / #]"
         * <br>Where the first # is shardId and the second # is shardTotal.
         *
         * @return A String representing the information used to build this shard.
         */
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

    /**
     * Gets the current {@link JDA.Status Status} of the JDA instance.
     *
     * @return Current JDA status.
     */
    @Nonnull
    Status getStatus();

    /**
     * The time in milliseconds that discord took to respond to our last heartbeat
     * <br>This roughly represents the WebSocket ping of this session
     *
     * <p><b>{@link RestAction RestAction} request times do not
     * correlate to this value!</b>
     *
     * <p>The {@link GatewayPingEvent GatewayPingEvent} indicates an update to this value.
     *
     * @return time in milliseconds between heartbeat and the heartbeat ack response
     *
     * @see    #getRestPing() Getting RestAction ping
     */
    long getGatewayPing();

    /**
     * The time in milliseconds that discord took to respond to a REST request.
     * <br>This will request the current user from the API and calculate the time the response took.
     *
     * <h2>Example</h2>
     * <pre><code>
     * jda.getRestPing().queue( (time) {@literal ->}
     *     channel.sendMessageFormat("Ping: %d ms", time).queue()
     * );
     * </code></pre>
     *
     * @return {@link RestAction RestAction} - Type: long
     *
     * @since  4.0.0
     *
     * @see    #getGatewayPing()
     */
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

    /**
     * This method will block until JDA has reached the specified connection status.
     *
     * <h2>Login Cycle</h2>
     * <ol>
     *  <li>{@link JDA.Status#INITIALIZING INITIALIZING}</li>
     *  <li>{@link JDA.Status#INITIALIZED INITIALIZED}</li>
     *  <li>{@link JDA.Status#LOGGING_IN LOGGING_IN}</li>
     *  <li>{@link JDA.Status#CONNECTING_TO_WEBSOCKET CONNECTING_TO_WEBSOCKET}</li>
     *  <li>{@link JDA.Status#IDENTIFYING_SESSION IDENTIFYING_SESSION}</li>
     *  <li>{@link JDA.Status#AWAITING_LOGIN_CONFIRMATION AWAITING_LOGIN_CONFIRMATION}</li>
     *  <li>{@link JDA.Status#LOADING_SUBSYSTEMS LOADING_SUBSYSTEMS}</li>
     *  <li>{@link JDA.Status#CONNECTED CONNECTED}</li>
     * </ol>
     *
     * @param  status
     *         The init status to wait for, once JDA has reached the specified
     *         stage of the startup cycle this method will return.
     *
     * @throws InterruptedException
     *         If this thread is interrupted while waiting
     * @throws IllegalArgumentException
     *         If the provided status is null or not an init status ({@link Status#isInit()})
     * @throws IllegalStateException
     *         If JDA is shutdown during this wait period
     *
     * @return The current JDA instance, for chaining convenience
     */
    @Nonnull
    default JDA awaitStatus(@Nonnull JDA.Status status) throws InterruptedException
    {
        //This is done to retain backwards compatible ABI as it would otherwise change the signature of the method
        // which would require recompilation for all users (including extension libraries)
        return awaitStatus(status, new JDA.Status[0]);
    }

    /**
     * This method will block until JDA has reached the specified connection status.
     *
     * <h2>Login Cycle</h2>
     * <ol>
     *  <li>{@link JDA.Status#INITIALIZING INITIALIZING}</li>
     *  <li>{@link JDA.Status#INITIALIZED INITIALIZED}</li>
     *  <li>{@link JDA.Status#LOGGING_IN LOGGING_IN}</li>
     *  <li>{@link JDA.Status#CONNECTING_TO_WEBSOCKET CONNECTING_TO_WEBSOCKET}</li>
     *  <li>{@link JDA.Status#IDENTIFYING_SESSION IDENTIFYING_SESSION}</li>
     *  <li>{@link JDA.Status#AWAITING_LOGIN_CONFIRMATION AWAITING_LOGIN_CONFIRMATION}</li>
     *  <li>{@link JDA.Status#LOADING_SUBSYSTEMS LOADING_SUBSYSTEMS}</li>
     *  <li>{@link JDA.Status#CONNECTED CONNECTED}</li>
     * </ol>
     *
     * @param  status
     *         The init status to wait for, once JDA has reached the specified
     *         stage of the startup cycle this method will return.
     * @param  failOn
     *         Optional failure states that will force a premature return
     *
     * @throws InterruptedException
     *         If this thread is interrupted while waiting
     * @throws IllegalArgumentException
     *         If the provided status is null or not an init status ({@link Status#isInit()})
     * @throws IllegalStateException
     *         If JDA is shutdown during this wait period
     *
     * @return The current JDA instance, for chaining convenience
     */
    @Nonnull
    JDA awaitStatus(@Nonnull JDA.Status status, @Nonnull JDA.Status... failOn) throws InterruptedException;

    /**
     * This method will block until JDA has reached the status {@link Status#CONNECTED}.
     * <br>This status means that JDA finished setting up its internal cache and is ready to be used.
     *
     * @throws InterruptedException
     *         If this thread is interrupted while waiting
     * @throws IllegalStateException
     *         If JDA is shutdown during this wait period
     *
     * @return The current JDA instance, for chaining convenience
     */
    @Nonnull
    default JDA awaitReady() throws InterruptedException
    {
        return awaitStatus(Status.CONNECTED);
    }

    /**
     * {@link ScheduledExecutorService} used to handle rate-limits for {@link RestAction}
     * executions. This is also used in other parts of JDA related to http requests.
     *
     * @return The {@link ScheduledExecutorService} used for http request handling
     *
     * @since  4.0.0
     */
    @Nonnull
    ScheduledExecutorService getRateLimitPool();

    /**
     * {@link ScheduledExecutorService} used to send WebSocket messages to discord.
     * <br>This involves initial setup of guilds as well as keeping the connection alive.
     *
     * @return The {@link ScheduledExecutorService} used for WebSocket transmissions
     *
     * @since  4.0.0
     */
    @Nonnull
    ScheduledExecutorService getGatewayPool();

    /**
     * {@link ExecutorService} used to handle {@link RestAction} callbacks
     * and completions. This is also used for handling {@link Message.Attachment} downloads
     * when needed.
     * <br>By default this uses the {@link ForkJoinPool#commonPool() CommonPool} of the runtime.
     *
     * @return The {@link ExecutorService} used for callbacks
     *
     * @since  4.0.0
     */
    @Nonnull
    ExecutorService getCallbackPool();

    /**
     * The {@link OkHttpClient} used for handling http requests from {@link RestAction RestActions}.
     *
     * @return The http client
     *
     * @since  4.0.0
     */
    @Nonnull
    OkHttpClient getHttpClient();

    /**
     * Direct access to audio (dis-)connect requests.
     * <br>This should not be used when normal audio operation is desired.
     *
     * <p>The correct way to open and close an audio connection is through the {@link Guild Guild's}
     * {@link AudioManager}.
     *
     * @return The {@link DirectAudioController} for this JDA instance
     *
     * @since  4.0.0
     */
    @Nonnull
    DirectAudioController getDirectAudioController();

    /**
     * Changes the internal EventManager.
     *
     * <p>The default EventManager is {@link InterfacedEventManager InterfacedEventListener}.
     * <br>There is also an {@link AnnotatedEventManager AnnotatedEventManager} available.
     *
     * @param  manager
     *         The new EventManager to use
     */
    void setEventManager(@Nullable IEventManager manager);

    /**
     * Adds all provided listeners to the event-listeners that will be used to handle events.
     * This uses the {@link InterfacedEventManager InterfacedEventListener} by default.
     * To switch to the {@link AnnotatedEventManager AnnotatedEventManager}, use {@link #setEventManager(IEventManager)}.
     *
     * Note: when using the {@link InterfacedEventManager InterfacedEventListener} (default),
     * given listener <b>must</b> be instance of {@link EventListener EventListener}!
     *
     * @param  listeners
     *         The listener(s) which will react to events.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     */
    void addEventListener(@Nonnull Object... listeners);

    /**
     * Removes all provided listeners from the event-listeners and no longer uses them to handle events.
     *
     * @param  listeners
     *         The listener(s) to be removed.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     */
    void removeEventListener(@Nonnull Object... listeners);

    /**
     * Immutable List of Objects that have been registered as EventListeners.
     *
     * @return List of currently registered Objects acting as EventListeners.
     */
    @Nonnull
    List<Object> getRegisteredListeners();

    /**
     * Constructs a new {@link Guild Guild} with the specified name
     * <br>Use the returned {@link GuildAction GuildAction} to provide
     * further details and settings for the resulting Guild!
     *
     * <p>This RestAction does not provide the resulting Guild!
     * It will be in a following {@link GuildJoinEvent GuildJoinEvent}.
     *
     * @param  name
     *         The name of the resulting guild
     *
     * @throws java.lang.IllegalStateException
     *         If the currently logged in account is from
     *         <ul>
     *             <li>{@link AccountType#CLIENT AccountType.CLIENT} and the account is in 100 or more guilds</li>
     *             <li>{@link AccountType#BOT AccountType.BOT} and the account is in 10 or more guilds</li>
     *         </ul>
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is empty, {@code null} or not between 2-100 characters
     *
     * @return {@link GuildAction GuildAction}
     *         <br>Allows for setting various details for the resulting Guild
     */
    @Nonnull
    @CheckReturnValue
    GuildAction createGuild(@Nonnull String name);

    /**
     * {@link CacheView CacheView} of
     * all cached {@link AudioManager AudioManagers} created for this JDA instance.
     * <br>AudioManagers are created when first retrieved via {@link Guild#getAudioManager() Guild.getAudioManager()}.
     * <u>Using this will perform better than calling {@code Guild.getAudioManager()} iteratively as that would cause many useless audio managers to be created!</u>
     *
     * <p>AudioManagers are cross-session persistent!
     *
     * @return {@link CacheView CacheView}
     */
    @Nonnull
    CacheView<AudioManager> getAudioManagerCache();

    /**
     * Immutable list of all created {@link AudioManager AudioManagers} for this JDA instance!
     *
     * @return Immutable list of all created AudioManager instances
     */
    @Nonnull
    default List<AudioManager> getAudioManagers()
    {
        return getAudioManagerCache().asList();
    }


    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link User Users} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<User> getUserCache();

    /**
     * An immutable list of all {@link User Users} that share a
     * {@link Guild Guild} with the currently logged in account.
     * <br>This list will never contain duplicates and represents all
     * {@link User Users} that JDA can currently see.
     *
     * <p>If the developer is sharding, then only users from guilds connected to the specifically logged in
     * shard will be returned in the List.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getUserCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Immutable list of all {@link User Users} that are visible to JDA.
     */
    @Nonnull
    default List<User> getUsers()
    {
        return getUserCache().asList();
    }

    /**
     * This returns the {@link User User} which has the same id as the one provided.
     * <br>If there is no visible user with an id that matches the provided one, this returns {@code null}.
     *
     * @param  id
     *         The id of the requested {@link User User}.
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link User User} with matching id.
     */
    @Nullable
    default User getUserById(@Nonnull String id)
    {
        return getUserCache().getElementById(id);
    }

    /**
     * This returns the {@link User User} which has the same id as the one provided.
     * <br>If there is no visible user with an id that matches the provided one, this returns {@code null}.
     *
     * @param  id
     *         The id of the requested {@link User User}.
     *
     * @return Possibly-null {@link User User} with matching id.
     */
    @Nullable
    default User getUserById(long id)
    {
        return getUserCache().getElementById(id);
    }

    /**
     * Searches for a user that has the matching Discord Tag.
     * <br>Format has to be in the form {@code Username#Discriminator} where the
     * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
     * must be exactly 4 digits.
     *
     * <p>This only checks users that are known to the currently logged in account (shard). If a user exists
     * with the tag that is not available in the {@link #getUserCache() User-Cache} it will not be detected.
     * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
     *
     * @param  tag
     *         The Discord Tag in the format {@code Username#Discriminator}
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided tag is null or not in the described format
     *
     * @return The {@link User} for the discord tag or null if no user has the provided tag
     */
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

    /**
     * Searches for a user that has the matching Discord Tag.
     * <br>Format has to be in the form {@code Username#Discriminator} where the
     * username must be between 2 and 32 characters (inclusive) matching the exact casing and the discriminator
     * must be exactly 4 digits.
     *
     * <p>This only checks users that are known to the currently logged in account (shard). If a user exists
     * with the tag that is not available in the {@link #getUserCache() User-Cache} it will not be detected.
     * <br>Currently Discord does not offer a way to retrieve a user by their discord tag.
     *
     * @param  username
     *         The name of the user
     * @param  discriminator
     *         The discriminator of the user
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided arguments are null or not in the described format
     *
     * @return The {@link User} for the discord tag or null if no user has the provided tag
     */
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

    /**
     * This immutable returns all {@link User Users} that have the same username as the one provided.
     * <br>If there are no {@link User Users} with the provided name, then this returns an empty list.
     *
     * <p><b>Note: </b> This does **not** consider nicknames, it only considers {@link User#getName()}
     *
     * @param  name
     *         The name of the requested {@link User Users}.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link User#getName()}.
     *
     * @return Possibly-empty immutable list of {@link User Users} that all have the same name as the provided name.
     */
    @Nonnull
    default List<User> getUsersByName(@Nonnull String name, boolean ignoreCase)
    {
        return getUserCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Gets all {@link Guild Guilds} that contain all given users as their members.
     *
     * @param  users
     *         The users which all the returned {@link Guild Guilds} must contain.
     *
     * @return Immutable list of all {@link Guild Guild} instances which have all {@link User Users} in them.
     *
     * @see    Guild#isMember(User)
     */
    @Nonnull
    List<Guild> getMutualGuilds(@Nonnull User... users);

    /**
     * Gets all {@link Guild Guilds} that contain all given users as their members.
     *
     * @param users
     *        The users which all the returned {@link Guild Guilds} must contain.
     *
     * @return Immutable list of all {@link Guild Guild} instances which have all {@link User Users} in them.
     */
    @Nonnull
    List<Guild> getMutualGuilds(@Nonnull Collection<User> users);

    /**
     * Attempts to retrieve a {@link User User} object based on the provided id.
     * <br>This first calls {@link #getUserById(long)}, and if the return is {@code null} then a request
     * is made to the Discord servers.
     *
     * <p>The returned {@link RestAction RestAction} can encounter the following Discord errors:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_USER ErrorResponse.UNKNOWN_USER}
     *     <br>Occurs when the provided id does not refer to a {@link User User}
     *     known by Discord. Typically occurs when developers provide an incomplete id (cut short).</li>
     * </ul>
     *
     * @param  id
     *         The id of the requested {@link User User}.
     *
     * @throws AccountTypeException
     *         This endpoint is {@link AccountType#BOT} only.
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     * @throws java.lang.IllegalArgumentException
     *         <ul>
     *             <li>If the provided id String is null.</li>
     *             <li>If the provided id String is empty.</li>
     *         </ul>
     *
     * @return {@link RestAction RestAction} - Type: {@link User User}
     *         <br>On request, gets the User with id matching provided id from Discord.
     */
    @Nonnull
    @CheckReturnValue
    RestAction<User> retrieveUserById(@Nonnull String id);

    /**
     * Attempts to retrieve a {@link User User} object based on the provided id.
     * <br>This first calls {@link #getUserById(long)}, and if the return is {@code null} then a request
     * is made to the Discord servers.
     *
     * <p>The returned {@link RestAction RestAction} can encounter the following Discord errors:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_USER ErrorResponse.UNKNOWN_USER}
     *     <br>Occurs when the provided id does not refer to a {@link User User}
     *     known by Discord. Typically occurs when developers provide an incomplete id (cut short).</li>
     * </ul>
     *
     * @param  id
     *         The id of the requested {@link User User}.
     *
     * @throws AccountTypeException
     *         This endpoint is {@link AccountType#BOT} only.
     *
     * @return {@link RestAction RestAction} - Type: {@link User User}
     *         <br>On request, gets the User with id matching provided id from Discord.
     */
    @Nonnull
    @CheckReturnValue
    RestAction<User> retrieveUserById(long id);

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Guild Guilds} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<Guild> getGuildCache();

    /**
     * An immutable List of all {@link Guild Guilds} that the logged account is connected to.
     * <br>If this account is not connected to any {@link Guild Guilds}, this will return an empty list.
     *
     * <p>If the developer is sharding ({@link JDABuilder#useSharding(int, int)}, then this list
     * will only contain the {@link Guild Guilds} that the shard is actually connected to.
     * Discord determines which guilds a shard is connect to using the following format:
     * <br>Guild connected if shardId == (guildId {@literal >>} 22) % totalShards;
     * <br>Source for formula: <a href="https://discordapp.com/developers/docs/topics/gateway#sharding">Discord Documentation</a>
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getGuildCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Possibly-empty immutable list of all the {@link Guild Guilds} that this account is connected to.
     */
    @Nonnull
    default List<Guild> getGuilds()
    {
        return getGuildCache().asList();
    }

    /**
     * This returns the {@link Guild Guild} which has the same id as the one provided.
     * <br>If there is no connected guild with an id that matches the provided one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link Guild Guild}.
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link Guild Guild} with matching id.
     */
    @Nullable
    default Guild getGuildById(@Nonnull String id)
    {
        return getGuildCache().getElementById(id);
    }

    /**
     * This returns the {@link Guild Guild} which has the same id as the one provided.
     * <br>If there is no connected guild with an id that matches the provided one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link Guild Guild}.
     *
     * @return Possibly-null {@link Guild Guild} with matching id.
     */
    @Nullable
    default Guild getGuildById(long id)
    {
        return getGuildCache().getElementById(id);
    }

    /**
     * An immutable list of all {@link Guild Guilds} that have the same name as the one provided.
     * <br>If there are no {@link Guild Guilds} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name of the requested {@link Guild Guilds}.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link Guild#getName()}.
     *
     * @return Possibly-empty immutable list of all the {@link Guild Guilds} that all have the same name as the provided name.
     */
    @Nonnull
    default List<Guild> getGuildsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getGuildCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Set of {@link Guild} IDs for guilds that were marked unavailable by the gateway.
     * <br>When a guild becomes unavailable a {@link GuildUnavailableEvent GuildUnavailableEvent}
     * is emitted and a {@link GuildAvailableEvent GuildAvailableEvent} is emitted
     * when it becomes available again. During the time a guild is unavailable it its not reachable through
     * cache such as {@link #getGuildById(long)}.
     *
     * @return Possibly-empty set of guild IDs for unavailable guilds
     */
    @Nonnull
    Set<String> getUnavailableGuilds();

    /**
     * Whether the guild is unavailable. If this returns true, the guild id should be in {@link #getUnavailableGuilds()}.
     *
     * @param  guildId
     *         The guild id
     *
     * @return True, if this guild is unavailable
     */
    boolean isUnavailable(long guildId);

    /**
     * Unified {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Role Roles} visible to this JDA session.
     *
     * @return Unified {@link SnowflakeCacheView SnowflakeCacheView}
     *
     * @see    CacheView#allSnowflakes(java.util.function.Supplier) CacheView.allSnowflakes(...)
     */
    @Nonnull
    SnowflakeCacheView<Role> getRoleCache();

    /**
     * All {@link Role Roles} this JDA instance can see. <br>This will iterate over each
     * {@link Guild Guild} retrieved from {@link #getGuilds()} and collect its {@link
     * Guild#getRoles() Guild.getRoles()}.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getRoleCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Immutable List of all visible Roles
     */
    @Nonnull
    default List<Role> getRoles()
    {
        return getRoleCache().asList();
    }

    /**
     * Retrieves the {@link Role Role} associated to the provided id. <br>This iterates
     * over all {@link Guild Guilds} and check whether a Role from that Guild is assigned
     * to the specified ID and will return the first that can be found.
     *
     * @param  id
     *         The id of the searched Role
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link Role Role} for the specified ID
     */
    @Nullable
    default Role getRoleById(@Nonnull String id)
    {
        return getRoleCache().getElementById(id);
    }

    /**
     * Retrieves the {@link Role Role} associated to the provided id. <br>This iterates
     * over all {@link Guild Guilds} and check whether a Role from that Guild is assigned
     * to the specified ID and will return the first that can be found.
     *
     * @param  id
     *         The id of the searched Role
     *
     * @return Possibly-null {@link Role Role} for the specified ID
     */
    @Nullable
    default Role getRoleById(long id)
    {
        return getRoleCache().getElementById(id);
    }

    /**
     * Retrieves all {@link Role Roles} visible to this JDA instance.
     * <br>This simply filters the Roles returned by {@link #getRoles()} with the provided name, either using
     * {@link String#equals(Object)} or {@link String#equalsIgnoreCase(String)} on {@link Role#getName()}.
     *
     * @param  name
     *         The name for the Roles
     * @param  ignoreCase
     *         Whether to use {@link String#equalsIgnoreCase(String)}
     *
     * @return Immutable List of all Roles matching the parameters provided.
     */
    @Nonnull
    default List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getRoleCache().getElementsByName(name, ignoreCase);
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     * <br>This checks if any of the channel types in this guild have the provided ID and returns the first match.
     *
     * <br>To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(String)}</li>
     *     <li>{@link #getVoiceChannelById(String)}</li>
     *     <li>{@link #getStoreChannelById(String)}</li>
     *     <li>{@link #getCategoryById(String)}</li>
     * </ul>
     *
     * @param  id
     *         The ID of the channel
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided ID is null
     * @throws java.lang.NumberFormatException
     *         If the provided ID is not a snowflake
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull String id)
    {
        return getGuildChannelById(MiscUtil.parseSnowflake(id));
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     * <br>This checks if any of the channel types in this guild have the provided ID and returns the first match.
     *
     * <br>To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(long)}</li>
     *     <li>{@link #getVoiceChannelById(long)}</li>
     *     <li>{@link #getStoreChannelById(long)}</li>
     *     <li>{@link #getCategoryById(long)}</li>
     * </ul>
     *
     * @param  id
     *         The ID of the channel
     *
     * @return The GuildChannel or null
     */
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

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     *
     * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
     * profit from a simple function to get the channel instance.
     * To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(String)}</li>
     *     <li>{@link #getVoiceChannelById(String)}</li>
     *     <li>{@link #getStoreChannelById(String)}</li>
     *     <li>{@link #getCategoryById(String)}</li>
     * </ul>
     *
     * @param  type
     *         The {@link ChannelType}
     * @param  id
     *         The ID of the channel
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided ID is null
     * @throws java.lang.NumberFormatException
     *         If the provided ID is not a snowflake
     *
     * @return The GuildChannel or null
     */
    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, @Nonnull String id)
    {
        return getGuildChannelById(type, MiscUtil.parseSnowflake(id));
    }

    /**
     * Get {@link GuildChannel GuildChannel} for the provided ID.
     *
     * <br>This is meant for systems that use a dynamic {@link ChannelType} and can
     * profit from a simple function to get the channel instance.
     * To get more specific channel types you can use one of the following:
     * <ul>
     *     <li>{@link #getTextChannelById(long)}</li>
     *     <li>{@link #getVoiceChannelById(long)}</li>
     *     <li>{@link #getStoreChannelById(long)}</li>
     *     <li>{@link #getCategoryById(long)}</li>
     * </ul>
     *
     * @param  type
     *         The {@link ChannelType}
     * @param  id
     *         The ID of the channel
     *
     * @return The GuildChannel or null
     */
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

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Category Categories} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<Category> getCategoryCache();

    /**
     * Gets the {@link Category Category} that matches the provided id.
     * <br>If there is no matching {@link Category Category} this returns {@code null}.
     *
     * @param  id
     *         The snowflake ID of the wanted Category
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided ID is not a valid {@code long}
     *
     * @return Possibly-null {@link Category Category} for the provided ID.
     */
    @Nullable
    default Category getCategoryById(@Nonnull String id)
    {
        return getCategoryCache().getElementById(id);
    }

    /**
     * Gets the {@link Category Category} that matches the provided id. <br>If there is no
     * matching {@link Category Category} this returns {@code null}.
     *
     * @param  id
     *         The snowflake ID of the wanted Category
     *
     * @return Possibly-null {@link Category Category} for the provided ID.
     */
    @Nullable
    default Category getCategoryById(long id)
    {
        return getCategoryCache().getElementById(id);
    }

    /**
     * Gets all {@link Category Categories} visible to the currently logged in account.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getCategoryCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable list of all visible {@link Category Categories}.
     */
    @Nonnull
    default List<Category> getCategories()
    {
        return getCategoryCache().asList();
    }

    /**
     * Gets a list of all {@link Category Categories} that have the same name as the one
     * provided. <br>If there are no matching categories this will return an empty list.
     *
     * @param  name
     *         The name to check
     * @param  ignoreCase
     *         Whether to ignore case on name checking
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is {@code null}
     *
     * @return Immutable list of all categories matching the provided name
     */
    @Nonnull
    default List<Category> getCategoriesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getCategoryCache().getElementsByName(name, ignoreCase);
    }

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link StoreChannel StoreChannels} visible to this JDA session.
     * <br>TextChannels are sorted according to their position.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<StoreChannel> getStoreChannelCache();

    /**
     * Gets a {@link StoreChannel StoreChannel} that has the same id as the
     * one provided.
     * <br>If there is no {@link StoreChannel StoreChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link StoreChannel StoreChannel}.
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link StoreChannel StoreChannel} with matching id.
     */
    @Nullable
    default StoreChannel getStoreChannelById(@Nonnull String id)
    {
        return getStoreChannelCache().getElementById(id);
    }

    /**
     * Gets a {@link StoreChannel StoreChannel} that has the same id as the
     * one provided.
     * <br>If there is no {@link StoreChannel StoreChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link StoreChannel StoreChannel}.
     *
     * @return Possibly-null {@link StoreChannel StoreChannel} with matching id.
     */
    @Nullable
    default StoreChannel getStoreChannelById(long id)
    {
        return getStoreChannelCache().getElementById(id);
    }

    /**
     * Gets all {@link StoreChannel StoreChannels} of all connected
     * {@link Guild Guilds}.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getStoreChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Possibly-empty immutable List of all known {@link StoreChannel StoreChannel}.
     */
    @Nonnull
    default List<StoreChannel> getStoreChannels()
    {
        return getStoreChannelCache().asList();
    }

    /**
     * An unmodifiable list of all {@link StoreChannel StoreChannels} that have the same name as the one provided.
     * <br>If there are no {@link StoreChannel StoreChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name used to filter the returned {@link StoreChannel StoreChannels}.
     * @param  ignoreCase
     *         Determines if the comparison ignores case when comparing. True - case insensitive.
     *
     * @return Possibly-empty immutable list of all StoreChannels with names that match the provided name.
     */
    @Nonnull
    default List<StoreChannel> getStoreChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getStoreChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link TextChannel TextChannels} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<TextChannel> getTextChannelCache();

    /**
     * An unmodifiable List of all {@link TextChannel TextChannels} of all connected
     * {@link Guild Guilds}.
     *
     * <p><b>Note:</b> just because a {@link TextChannel TextChannel} is present in this list does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is possible that you will see fewer channels than this returns. This is because the discord
     * client hides any {@link TextChannel TextChannel} that you don't have the
     * {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getTextChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Possibly-empty list of all known {@link TextChannel TextChannels}.
     */
    @Nonnull
    default List<TextChannel> getTextChannels()
    {
        return getTextChannelCache().asList();
    }

    /**
     * This returns the {@link TextChannel TextChannel} which has the same id as the one provided.
     * <br>If there is no known {@link TextChannel TextChannel} with an id that matches the
     * provided one, then this returns {@code null}.
     *
     * <p><b>Note:</b> just because a {@link TextChannel TextChannel} is present does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is you will not see the channel that this returns. This is because the discord client
     * hides any {@link TextChannel TextChannel} that you don't have the
     * {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @param  id
     *         The id of the {@link TextChannel TextChannel}.
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link TextChannel TextChannel} with matching id.
     */
    @Nullable
    default TextChannel getTextChannelById(@Nonnull String id)
    {
        return getTextChannelCache().getElementById(id);
    }

    /**
     * This returns the {@link TextChannel TextChannel} which has the same id as the one provided.
     * <br>If there is no known {@link TextChannel TextChannel} with an id that matches the
     * provided one, then this returns {@code null}.
     *
     * <p><b>Note:</b> just because a {@link TextChannel TextChannel} is present does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is you will not see the channel that this returns. This is because the discord client
     * hides any {@link TextChannel TextChannel} that you don't have the
     * {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @param  id
     *         The id of the {@link TextChannel TextChannel}.
     *
     * @return Possibly-null {@link TextChannel TextChannel} with matching id.
     */
    @Nullable
    default TextChannel getTextChannelById(long id)
    {
        return getTextChannelCache().getElementById(id);
    }

    /**
     * An unmodifiable list of all {@link TextChannel TextChannels} that have the same name as the one provided.
     * <br>If there are no {@link TextChannel TextChannels} with the provided name, then this returns an empty list.
     *
     * <p><b>Note:</b> just because a {@link TextChannel TextChannel} is present in this list does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is possible that you will see fewer channels than this returns. This is because the discord client
     * hides any {@link TextChannel TextChannel} that you don't have the
     * {@link Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @param  name
     *         The name of the requested {@link TextChannel TextChannels}.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link TextChannel#getName()}.
     *
     * @return Possibly-empty list of all the {@link TextChannel TextChannels} that all have the
     *         same name as the provided name.
     */
    @Nonnull
    default List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getTextChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link VoiceChannel VoiceChannels} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<VoiceChannel> getVoiceChannelCache();

    /**
     * An unmodifiable list of all {@link VoiceChannel VoiceChannels} of all connected
     * {@link Guild Guilds}.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getVoiceChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Possible-empty list of all known {@link VoiceChannel VoiceChannels}.
     */
    @Nonnull
    default List<VoiceChannel> getVoiceChannels()
    {
        return getVoiceChannelCache().asList();
    }

    /**
     * This returns the {@link VoiceChannel VoiceChannel} which has the same id as the one provided.
     * <br>If there is no known {@link VoiceChannel VoiceChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link VoiceChannel VoiceChannel}.
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
     */
    @Nullable
    default VoiceChannel getVoiceChannelById(@Nonnull String id)
    {
        return getVoiceChannelCache().getElementById(id);
    }

    /**
     * This returns the {@link VoiceChannel VoiceChannel} which has the same id as the one provided.
     * <br>If there is no known {@link VoiceChannel VoiceChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link VoiceChannel VoiceChannel}.
     *
     * @return Possibly-null {@link VoiceChannel VoiceChannel} with matching id.
     */
    @Nullable
    default VoiceChannel getVoiceChannelById(long id)
    {
        return getVoiceChannelCache().getElementById(id);
    }

    /**
     * An unmodifiable list of all {@link VoiceChannel VoiceChannels} that have the same name as the one provided.
     * <br>If there are no {@link VoiceChannel VoiceChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name of the requested {@link VoiceChannel VoiceChannels}.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link VoiceChannel#getName()}.
     *
     * @return Possibly-empty list of all the {@link VoiceChannel VoiceChannels} that all have the
     *         same name as the provided name.
     *
     * @deprecated
     *         Replace with {@link #getVoiceChannelsByName(String, boolean)}
     */
    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("jda.getVoiceChannelsByName(name, ignoreCase)")
    default List<VoiceChannel> getVoiceChannelByName(@Nonnull String name, boolean ignoreCase)
    {
        return getVoiceChannelsByName(name, ignoreCase);
    }

    /**
     * An unmodifiable list of all {@link VoiceChannel VoiceChannels} that have the same name as the one provided.
     * <br>If there are no {@link VoiceChannel VoiceChannels} with the provided name, then this returns an empty list.
     *
     * @param  name
     *         The name of the requested {@link VoiceChannel VoiceChannels}.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link VoiceChannel#getName()}.
     *
     * @return Possibly-empty list of all the {@link VoiceChannel VoiceChannels} that all have the
     *         same name as the provided name.
     */
    @Nonnull
    default List<VoiceChannel> getVoiceChannelsByName(@Nonnull String name, boolean ignoreCase)
    {
        return getVoiceChannelCache().getElementsByName(name, ignoreCase);
    }

    /**
     * {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link PrivateChannel PrivateChannels} visible to this JDA session.
     *
     * @return {@link SnowflakeCacheView SnowflakeCacheView}
     */
    @Nonnull
    SnowflakeCacheView<PrivateChannel> getPrivateChannelCache();

    /**
     * An unmodifiable list of all known {@link PrivateChannel PrivateChannels}.
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getPrivateChannelCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return Possibly-empty list of all {@link PrivateChannel PrivateChannels}.
     */
    @Nonnull
    default List<PrivateChannel> getPrivateChannels()
    {
        return getPrivateChannelCache().asList();
    }

    /**
     * This returns the {@link PrivateChannel PrivateChannel} which has the same id as the one provided.
     * <br>If there is no known {@link PrivateChannel PrivateChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link PrivateChannel PrivateChannel}.
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return Possibly-null {@link PrivateChannel PrivateChannel} with matching id.
     */
    @Nullable
    default PrivateChannel getPrivateChannelById(@Nonnull String id)
    {
        return getPrivateChannelCache().getElementById(id);
    }

    /**
     * This returns the {@link PrivateChannel PrivateChannel} which has the same id as the one provided.
     * <br>If there is no known {@link PrivateChannel PrivateChannel} with an id that matches the provided
     * one, then this returns {@code null}.
     *
     * @param  id
     *         The id of the {@link PrivateChannel PrivateChannel}.
     *
     * @return Possibly-null {@link PrivateChannel PrivateChannel} with matching id.
     */
    @Nullable
    default PrivateChannel getPrivateChannelById(long id)
    {
        return getPrivateChannelCache().getElementById(id);
    }

    /**
     * Unified {@link SnowflakeCacheView SnowflakeCacheView} of
     * all cached {@link Emote Emotes} visible to this JDA session.
     *
     * @return Unified {@link SnowflakeCacheView SnowflakeCacheView}
     *
     * @see    CacheView#allSnowflakes(java.util.function.Supplier) CacheView.allSnowflakes(...)
     */
    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();

    /**
     * A collection of all to us known emotes (managed/restricted included).
     * <br>This will be empty if {@link CacheFlag#EMOTE} is disabled.
     *
     * <p><b>Hint</b>: To check whether you can use an {@link Emote Emote} in a specific
     * context you can use {@link Emote#canInteract(Member)} or {@link
     * Emote#canInteract(User, MessageChannel)}
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * <p>This copies the backing store into a list. This means every call
     * creates a new list with O(n) complexity. It is recommended to store this into
     * a local variable or use {@link #getEmoteCache()} and use its more efficient
     * versions of handling these values.
     *
     * @return An immutable list of Emotes (which may or may not be available to usage).
     */
    @Nonnull
    default List<Emote> getEmotes()
    {
        return getEmoteCache().asList();
    }

    /**
     * Retrieves an emote matching the specified {@code id} if one is available in our cache.
     * <br>This will be null if {@link CacheFlag#EMOTE} is disabled.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * @param  id
     *         The id of the requested {@link Emote}.
     *
     * @throws java.lang.NumberFormatException
     *         If the provided {@code id} cannot be parsed by {@link Long#parseLong(String)}
     *
     * @return An {@link Emote Emote} represented by this id or null if none is found in
     *         our cache.
     */
    @Nullable
    default Emote getEmoteById(@Nonnull String id)
    {
        return getEmoteCache().getElementById(id);
    }

    /**
     * Retrieves an emote matching the specified {@code id} if one is available in our cache.
     * <br>This will be null if {@link CacheFlag#EMOTE} is disabled.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * @param  id
     *         The id of the requested {@link Emote}.
     *
     * @return An {@link Emote Emote} represented by this id or null if none is found in
     *         our cache.
     */
    @Nullable
    default Emote getEmoteById(long id)
    {
        return getEmoteCache().getElementById(id);
    }

    /**
     * An unmodifiable list of all {@link Emote Emotes} that have the same name as the one
     * provided. <br>If there are no {@link Emote Emotes} with the provided name, then
     * this returns an empty list.
     * <br>This will be empty if {@link CacheFlag#EMOTE} is disabled.
     *
     * <p><b>Unicode emojis are not included as {@link Emote Emote}!</b>
     *
     * @param  name
     *         The name of the requested {@link Emote Emotes}. Without colons.
     * @param  ignoreCase
     *         Whether to ignore case or not when comparing the provided name to each {@link
     *         Emote#getName()}.
     *
     * @return Possibly-empty list of all the {@link Emote Emotes} that all have the same
     *         name as the provided name.
     */
    @Nonnull
    default List<Emote> getEmotesByName(@Nonnull String name, boolean ignoreCase)
    {
        return getEmoteCache().getElementsByName(name, ignoreCase);
    }

    /**
     * The EventManager used by this JDA instance.
     *
     * @return The {@link IEventManager}
     */
    @Nonnull
    IEventManager getEventManager();

    /**
     * Returns the currently logged in account represented by {@link SelfUser SelfUser}.
     * <br>Account settings <b>cannot</b> be modified using this object. If you wish to modify account settings please
     * use the AccountManager which is accessible by {@link SelfUser#getManager()}.
     *
     * @return The currently logged in account.
     */
    @Nonnull
    SelfUser getSelfUser();

    /**
     * The {@link Presence Presence} controller for the current session.
     * <br>Used to set {@link Activity} and {@link OnlineStatus} information.
     *
     * @return The never-null {@link Presence Presence} for this session.
     */
    @Nonnull
    Presence getPresence();

    /**
     * The shard information used when creating this instance of JDA.
     * <br>Represents the information provided to {@link JDABuilder#useSharding(int, int)}.
     *
     * @return The shard information for this shard
     */
    @Nonnull
    ShardInfo getShardInfo();

    /**
     * The login token that is currently being used for Discord authentication.
     *
     * @return Never-null, 18 character length string containing the auth token.
     */
    @Nonnull
    String getToken();

    /**
     * This value is the total amount of JSON responses that discord has sent.
     * <br>This value resets every time the websocket has to perform a full reconnect (not resume).
     *
     * @return Never-negative long containing total response amount.
     */
    long getResponseTotal();

    /**
     * This value is the maximum amount of time, in seconds, that JDA will wait between reconnect attempts.
     * <br>Can be set using {@link JDABuilder#setMaxReconnectDelay(int) JDABuilder.setMaxReconnectDelay(int)}.
     *
     * @return The maximum amount of time JDA will wait between reconnect attempts in seconds.
     */
    int getMaxReconnectDelay();

    /**
     * Sets whether or not JDA should try to automatically reconnect if a connection-error is encountered.
     * <br>This will use an incremental reconnect (timeouts are increased each time an attempt fails).
     *
     * <p>Default is <b>true</b>.
     *
     * @param  reconnect If true - enables autoReconnect
     */
    void setAutoReconnect(boolean reconnect);

    /**
     * Whether the Requester should retry when
     * a {@link java.net.SocketTimeoutException SocketTimeoutException} occurs.
     *
     * @param  retryOnTimeout
     *         True, if the Request should retry once on a socket timeout
     */
    void setRequestTimeoutRetry(boolean retryOnTimeout);

    /**
     * USed to determine whether or not autoReconnect is enabled for JDA.
     *
     * @return True if JDA will attempt to automatically reconnect when a connection-error is encountered.
     */
    boolean isAutoReconnect();

    /**
     * Used to determine if JDA will process MESSAGE_DELETE_BULK messages received from Discord as a single
     * {@link MessageBulkDeleteEvent MessageBulkDeleteEvent} or split
     * the deleted messages up and fire multiple {@link MessageDeleteEvent MessageDeleteEvents},
     * one for each deleted message.
     *
     * <p>By default, JDA will separate the bulk delete event into individual delete events, but this isn't as efficient as
     * handling a single event would be. It is recommended that BulkDelete Splitting be disabled and that the developer
     * should instead handle the {@link MessageBulkDeleteEvent MessageBulkDeleteEvent}
     *
     * @return Whether or not JDA currently handles the BULK_MESSAGE_DELETE event by splitting it into individual MessageDeleteEvents or not.
     */
    boolean isBulkDeleteSplittingEnabled();

    /**
     * Shuts down this JDA instance, closing all its connections.
     * After this command is issued the JDA Instance can not be used anymore.
     * Already enqueued {@link RestAction RestActions} are still going to be executed.
     *
     * <p>If you want this instance to shutdown without executing, use {@link #shutdownNow() shutdownNow()}
     *
     * @see #shutdownNow()
     */
    void shutdown();

    /**
     * Shuts down this JDA instance instantly, closing all its connections.
     * After this command is issued the JDA Instance can not be used anymore.
     * This will also cancel all queued {@link RestAction RestActions}.
     *
     * <p>If you want this instance to shutdown without cancelling enqueued RestActions use {@link #shutdown() shutdown()}
     *
     * @see #shutdown()
     */
    void shutdownNow();

    ///**
    // * Installs an auxiliary cable into the given port of your system.
    // *
    // * @param  port
    // *         The port in which the cable should be installed.
    // *
    // * @return {@link me.syari.ss.discord.api.requests.restaction.AuditableRestAction AuditableRestAction}{@literal <}{@link Void}{@literal >}
    // */
    //AuditableRestAction<Void> installAuxiliaryCable(int port);

    /**
     * The {@link AccountType} of the currently logged in account.
     * <br>Used when determining functions that are restricted based on the type of account.
     *
     * @return The current AccountType.
     */
    @Nonnull
    AccountType getAccountType();

    /**
     * Retrieves the {@link ApplicationInfo ApplicationInfo} for
     * the application that owns the logged in Bot-Account.
     * <br>This contains information about the owner of the currently logged in bot account!
     *
     * @throws AccountTypeException
     *         If the currently logged in account is not from {@link AccountType#BOT AccountType.BOT}
     *
     * @return {@link RestAction RestAction} - Type: {@link ApplicationInfo ApplicationInfo}
     *         <br>The {@link ApplicationInfo ApplicationInfo} of the bot's application.
     */
    @Nonnull
    @CheckReturnValue
    RestAction<ApplicationInfo> retrieveApplicationInfo();

    /**
     * Creates an authorization invite url for the currently logged in Bot-Account.
     * <br>Example Format:
     * {@code https://discordapp.com/oauth2/authorize?scope=bot&client_id=288202953599221761&permissions=8}
     *
     * <p><b>Hint:</b> To enable a pre-selected Guild of choice append the parameter {@code &guild_id=YOUR_GUILD_ID}
     *
     * @param  permissions
     *         The permissions to use in your invite, these can be changed by the link user.
     *         <br>If no permissions are provided the {@code permissions} parameter is omitted
     *
     * @throws AccountTypeException
     *         If the currently logged in account is not from {@link AccountType#BOT AccountType.BOT}
     *
     * @return A valid OAuth2 invite url for the currently logged in Bot-Account
     */
    @Nonnull
    String getInviteUrl(@Nullable Permission... permissions);

    /**
     * Creates an authorization invite url for the currently logged in Bot-Account.
     * <br>Example Format:
     * {@code https://discordapp.com/oauth2/authorize?scope=bot&client_id=288202953599221761&permissions=8}
     *
     * <p><b>Hint:</b> To enable a pre-selected Guild of choice append the parameter {@code &guild_id=YOUR_GUILD_ID}
     *
     * @param  permissions
     *         The permissions to use in your invite, these can be changed by the link user.
     *         <br>If no permissions are provided the {@code permissions} parameter is omitted
     *
     * @throws AccountTypeException
     *         If the currently logged in account is not from {@link AccountType#BOT AccountType.BOT}
     *
     * @return A valid OAuth2 invite url for the currently logged in Bot-Account
     */
    @Nonnull
    String getInviteUrl(@Nullable Collection<Permission> permissions);

    /**
     * Returns the {@link ShardManager ShardManager} that manages this JDA instances or null if this instance is not managed
     * by any {@link ShardManager ShardManager}.
     *
     * @return The corresponding ShardManager or {@code null} if there is no such manager
     */
    @Nullable
    ShardManager getShardManager();

    /**
     * Retrieves a {@link Webhook Webhook} by its id.
     *
     * <p>Possible {@link ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>We do not have the required permissions</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>A webhook with this id does not exist</li>
     * </ul>
     *
     * @param  webhookId
     *         The webhook id
     *
     * @throws IllegalArgumentException
     *         If the {@code webhookId} is null or empty
     *
     * @return {@link RestAction RestAction} - Type: {@link Webhook Webhook}
     *          <br>The webhook object.
     *
     * @see    Guild#retrieveWebhooks()
     * @see    TextChannel#retrieveWebhooks()
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Webhook> retrieveWebhookById(@Nonnull String webhookId);

    /**
     * Retrieves a {@link Webhook Webhook} by its id.
     *
     * <p>Possible {@link ErrorResponse ErrorResponses} caused by
     * the returned {@link RestAction RestAction} include the following:
     * <ul>
     *     <li>{@link ErrorResponse#MISSING_PERMISSIONS MISSING_PERMISSIONS}
     *     <br>We do not have the required permissions</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_WEBHOOK UNKNOWN_WEBHOOK}
     *     <br>A webhook with this id does not exist</li>
     * </ul>
     *
     * @param  webhookId
     *         The webhook id
     *
     * @return {@link RestAction RestAction} - Type: {@link Webhook Webhook}
     *          <br>The webhook object.
     *
     * @see    Guild#retrieveWebhooks()
     * @see    TextChannel#retrieveWebhooks()
     */
    @Nonnull
    @CheckReturnValue
    default RestAction<Webhook> retrieveWebhookById(long webhookId)
    {
        return retrieveWebhookById(Long.toUnsignedString(webhookId));
    }

    /**
     * Installs an auxiliary port for audio transfer.
     *
     * @throws IllegalStateException
     *         If this is a headless environment or no port is available
     *
     * @return {@link AuditableRestAction} - Type: int
     *         Provides the resulting used port
     */
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
