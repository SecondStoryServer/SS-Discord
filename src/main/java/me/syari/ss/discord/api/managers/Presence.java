

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.OnlineStatus;
import me.syari.ss.discord.api.entities.Activity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The Presence associated with the provided JDA instance
 *
 * @since  3.0
 */
public interface Presence
{
    /**
     * The JDA instance of this Presence
     *
     * @return The current JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * The current OnlineStatus for this session.
     * <br>This might not be what the Discord Client displays due to session clashing!
     *
     * @return The {@link OnlineStatus OnlineStatus}
     *         of the current session
     */
    @Nonnull
    OnlineStatus getStatus();

    /**
     * The current Activity for this session.
     * <br>This might not be what the Discord Client displays due to session clashing!
     *
     * @return The {@link Activity Activity}
     *         of the current session or null if no activity is set
     */
    @Nullable
    Activity getActivity();

    /**
     * Whether the current session is marked as afk or not.
     *
     * <p>This is relevant to client accounts to monitor
     * whether new messages should trigger mobile push-notifications.
     *
     * @return True if this session is marked as afk
     */
    boolean isIdle();

    /**
     * Sets the {@link OnlineStatus OnlineStatus} for this session
     *
     * @throws IllegalArgumentException
     *         if the provided OnlineStatus is {@link OnlineStatus#UNKNOWN UNKNOWN}
     *
     * @param  status
     *         the {@link OnlineStatus OnlineStatus}
     *         to be used (OFFLINE/null {@literal ->} INVISIBLE)
     */
    void setStatus(@Nullable OnlineStatus status);

    /**
     * Sets the {@link Activity Activity} for this session.
     * <br>A Activity can be retrieved via {@link Activity#playing(String)}.
     * For streams you provide a valid streaming url as second parameter
     *
     * <p>Examples:
     * <br>{@code presence.setActivity(Activity.playing("Thrones"));}
     * <br>{@code presence.setActivity(Activity.streaming("Thrones", "https://twitch.tv/EasterEggs"));}
     *
     * @param  activity
     *         A {@link Activity Activity} instance or null to reset
     *
     * @see    Activity#playing(String)
     * @see    Activity#streaming(String, String)
     */
    void setActivity(@Nullable Activity activity);

    /**
     * Sets whether this session should be marked as afk or not
     *
     * <p>This is relevant to client accounts to monitor
     * whether new messages should trigger mobile push-notifications.
     *
     * @param idle
     *        boolean
     */
    void setIdle(boolean idle);

    /**
     * Sets all presence fields of this session.
     *
     * @param  status
     *         The {@link OnlineStatus OnlineStatus} for this session
     *         (See {@link #setStatus(OnlineStatus)})
     * @param  activity
     *         The {@link Activity Activity} for this session
     *         (See {@link #setActivity(Activity)} for more info)
     * @param  idle
     *         Whether to mark this session as idle (useful for client accounts {@link #setIdle(boolean)})
     *
     * @throws java.lang.IllegalArgumentException
     *         If the specified OnlineStatus is {@link OnlineStatus#UNKNOWN UNKNOWN}
     */
    void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity, boolean idle);

    /**
     * Sets two presence fields of this session.
     * <br>The third field stays untouched.
     *
     * @param  status
     *         The {@link OnlineStatus OnlineStatus} for this session
     *         (See {@link #setStatus(OnlineStatus)})
     * @param  activity
     *         The {@link Activity Activity} for this session
     *         (See {@link #setActivity(Activity)} for more info)
     *
     * @throws java.lang.IllegalArgumentException
     *         If the specified OnlineStatus is {@link OnlineStatus#UNKNOWN UNKNOWN}
     */
    void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity);

    /**
     * Sets two presence fields of this session.
     * <br>The third field stays untouched.
     *
     * @param  status
     *         The {@link OnlineStatus OnlineStatus} for this session
     *         (See {@link #setStatus(OnlineStatus)})
     * @param  idle
     *         Whether to mark this session as idle (useful for client accounts {@link #setIdle(boolean)})
     *
     * @throws java.lang.IllegalArgumentException
     *         If the specified OnlineStatus is {@link OnlineStatus#UNKNOWN UNKNOWN}
     */
    void setPresence(@Nullable OnlineStatus status, boolean idle);

    /**
     * Sets two presence fields of this session.
     * <br>The third field stays untouched.
     *
     * @param  activity
     *         The {@link Activity Activity} for this session
     *         (See {@link #setActivity(Activity)} for more info)
     * @param  idle
     *         Whether to mark this session as idle (useful for client accounts {@link #setIdle(boolean)})
     */
    void setPresence(@Nullable Activity activity, boolean idle);
}
