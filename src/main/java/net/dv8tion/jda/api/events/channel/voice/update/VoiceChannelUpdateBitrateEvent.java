
package net.dv8tion.jda.api.events.channel.voice.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s bitrate changed.
 *
 * <p>Can be sued to get affected VoiceChannel, affected Guild and previous bitrate.
 *
 * <p>Identifier: {@code bitrate}
 */
public class VoiceChannelUpdateBitrateEvent extends GenericVoiceChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "bitrate";

    public VoiceChannelUpdateBitrateEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldBitrate)
    {
        super(api, responseNumber, channel, oldBitrate, channel.getBitrate(), IDENTIFIER);
    }

    /**
     * The old bitrate
     *
     * @return The old bitrate
     */
    public int getOldBitrate()
    {
        return getOldValue();
    }

    /**
     * The new bitrate
     *
     * @return The new bitrate
     */
    public int getNewBitrate()
    {
        return getNewValue();
    }
}
