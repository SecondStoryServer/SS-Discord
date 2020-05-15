

package me.syari.ss.discord.api.audio.hooks;

import me.syari.ss.discord.api.audio.AudioReceiveHandler;
import me.syari.ss.discord.api.audio.CombinedAudio;
import me.syari.ss.discord.api.audio.SpeakingMode;
import me.syari.ss.discord.api.audio.UserAudio;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Used to monitor an audio connection, ping, and speaking users.
 * <br>This provides functionality similar to the functionalities present in the Discord client related to an audio connection.
 */
public interface ConnectionListener
{
    /**
     * Called when JDA send a heartbeat packet to Discord and Discord sends an acknowledgement. The time difference
     * between sending and receiving the acknowledgement is calculated as the ping.
     *
     * @param  ping
     *         The time, in milliseconds, for round-trip packet travel to discord.
     */
    void onPing(long ping);

    /**
     * Called when the status of the audio channel changes. Used to track the connection state of the audio connection
     * for easy debug and status display for clients.
     *
     * @param  status
     *         The new {@link ConnectionStatus ConnectionStatus} of the audio connection.
     */
    void onStatusChange(@Nonnull ConnectionStatus status);

    /**
     * This method is an easy way to detect if a user is talking. Discord sends us an event when a user starts or stops
     * talking and it is parallel to the audio socket, so this event could come milliseconds before or after audio begins
     * or stops. This method is brilliant for clients wanting to display that a user is currently talking.
     * <p>
     * Unlike the {@link AudioReceiveHandler#handleCombinedAudio(CombinedAudio)
     * AudioReceiveHandler.handleCombinedAudio(CombinedAudio)} and
     * {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} methods which are
     * fired extremely often, this method is fired as a flag for the beginning and ending of audio transmission, and as such
     * is only fired when that changes. So while the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method is fired every time JDA receives audio data from Discord,
     * this is only fired when that stream starts and when it stops.
     * <br>If the user speaks for 3 minutes straight without ever stopping, then this would fire 2 times, once at the beginning
     * and once after 3 minutes when they stop talking even though the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method was fired thousands of times over the course of the 3 minutes.
     *
     * @param  user
     *         Never-null {@link User User} who's talking status has changed.
     * @param  speaking
     *         If true, the user has begun transmitting audio.
     */
    void onUserSpeaking(@Nonnull User user, boolean speaking);

    /**
     * This method is an easy way to detect if a user is talking. Discord sends us an event when a user starts or stops
     * talking and it is parallel to the audio socket, so this event could come milliseconds before or after audio begins
     * or stops. This method is brilliant for clients wanting to display that a user is currently talking.
     * <p>
     * Unlike the {@link AudioReceiveHandler#handleCombinedAudio(CombinedAudio)
     * AudioReceiveHandler.handleCombinedAudio(CombinedAudio)} and
     * {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} methods which are
     * fired extremely often, this method is fired as a flag for the beginning and ending of audio transmission, and as such
     * is only fired when that changes. So while the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method is fired every time JDA receives audio data from Discord,
     * this is only fired when that stream starts and when it stops.
     * <br>If the user speaks for 3 minutes straight without ever stopping, then this would fire 2 times, once at the beginning
     * and once after 3 minutes when they stop talking even though the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method was fired thousands of times over the course of the 3 minutes.
     *
     * @param  user
     *         Never-null {@link User User} who's talking status has changed.
     * @param  modes
     *         EnumSet, containing the active speaking modes.
     *         Empty if the user has stopped transmitting audio.
     *
     * @see    java.util.EnumSet EnumSet
     * @see    SpeakingMode SpeakingMode
     */
    default void onUserSpeaking(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes) {}


    /**
     * This method is an easy way to detect if a user is talking. Discord sends us an event when a user starts or stops
     * talking and it is parallel to the audio socket, so this event could come milliseconds before or after audio begins
     * or stops. This method is brilliant for clients wanting to display that a user is currently talking.
     * <p>
     * Unlike the {@link AudioReceiveHandler#handleCombinedAudio(CombinedAudio)
     * AudioReceiveHandler.handleCombinedAudio(CombinedAudio)} and
     * {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} methods which are
     * fired extremely often, this method is fired as a flag for the beginning and ending of audio transmission, and as such
     * is only fired when that changes. So while the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method is fired every time JDA receives audio data from Discord,
     * this is only fired when that stream starts and when it stops.
     * <br>If the user speaks for 3 minutes straight without ever stopping, then this would fire 2 times, once at the beginning
     * and once after 3 minutes when they stop talking even though the {@link AudioReceiveHandler#handleUserAudio(UserAudio)
     * AudioReceiveHandler.handleUserAudio(UserAudio)} method was fired thousands of times over the course of the 3 minutes.
     *
     * @param  user
     *         Never-null {@link User User} who's talking status has changed.
     * @param  speaking
     *         If true, the user has begun transmitting audio.
     * @param  soundshare
     *         If true, the user is using soundshare
     */
    default void onUserSpeaking(@Nonnull User user, boolean speaking, boolean soundshare) {}
}
