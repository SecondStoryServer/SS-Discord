
package net.dv8tion.jda.api.events.channel.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.VoiceChannel VoiceChannel} was deleted.
 *
 * <p>Can be used to get affected VoiceChannel or affected Guild.
 */
public class VoiceChannelDeleteEvent extends GenericVoiceChannelEvent
{
    public VoiceChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
