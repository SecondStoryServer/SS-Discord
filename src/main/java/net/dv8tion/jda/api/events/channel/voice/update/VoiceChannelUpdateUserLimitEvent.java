
package net.dv8tion.jda.api.events.channel.voice.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel}'s user limit changed.
 *
 * <p>Can be used to get affected VoiceChannel, affected Guild and previous user limit.
 *
 * <p>Identifier: {@code userlimit}
 */
public class VoiceChannelUpdateUserLimitEvent extends GenericVoiceChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "userlimit";

    public VoiceChannelUpdateUserLimitEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldUserLimit)
    {
        super(api, responseNumber, channel, oldUserLimit, channel.getUserLimit(), IDENTIFIER);
    }

    /**
     * The old userlimit
     *
     * @return The old userlimit
     */
    public int getOldUserLimit()
    {
        return getOldValue();
    }

    /**
     * The new userlimit
     *
     * @return The new userlimit
     */
    public int getNewUserLimit()
    {
        return getNewValue();
    }
}
