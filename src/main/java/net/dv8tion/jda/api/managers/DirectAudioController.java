

package net.dv8tion.jda.api.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Direct access to internal gateway communication.
 * <br>This should only be used if a {@link net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor VoiceDispatchInterceptor} has been provided.
 *
 * <p>For normal operation use {@link Guild#getAudioManager()} instead.
 */
public interface DirectAudioController
{
    /**
     * The associated JDA instance
     *
     * @return The JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Requests a voice server endpoint for connecting to the voice gateway.
     *
     * @param channel
     *        The channel to connect to
     *
     * @see   #reconnect(VoiceChannel)
     */
    void connect(@Nonnull VoiceChannel channel);

    /**
     * Requests to terminate the connection to a voice channel.
     *
     * @param guild
     *        The guild we were connected to
     *
     * @see   #reconnect(VoiceChannel)
     */
    void disconnect(@Nonnull Guild guild);

    /**
     * Requests to reconnect to the voice channel in the target guild.
     *
     * @param channel
     *        The channel we were connected to
     */
    void reconnect(@Nonnull VoiceChannel channel);
}
