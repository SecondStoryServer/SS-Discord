
package me.syari.ss.discord.api.events.channel.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class VoiceChannelDeleteEvent extends GenericVoiceChannelEvent
{
    public VoiceChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
