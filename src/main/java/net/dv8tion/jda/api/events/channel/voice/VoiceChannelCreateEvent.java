
package net.dv8tion.jda.api.events.channel.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} was created.
 *
 * <p>Can be used to get affected VoiceChannel.
 */
public class VoiceChannelCreateEvent extends GenericVoiceChannelEvent
{
    public VoiceChannelCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
