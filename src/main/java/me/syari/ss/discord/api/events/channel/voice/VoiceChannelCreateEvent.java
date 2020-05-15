
package me.syari.ss.discord.api.events.channel.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link VoiceChannel VoiceChannel} was created.
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
