
package net.dv8tion.jda.api.events.channel.voice.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s position changed.
 *
 * <p>Can be used to get affected VoiceChannel, affected Guild and previous position.
 *
 * <p>Identifier: {@code position}
 */
public class VoiceChannelUpdatePositionEvent extends GenericVoiceChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public VoiceChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldPosition)
    {
        super(api, responseNumber, channel, oldPosition, channel.getPositionRaw(), IDENTIFIER);
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPosition()
    {
        return getOldValue();
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }
}
