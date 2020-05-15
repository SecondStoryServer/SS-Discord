
package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class VoiceChannelUpdateBitrateEvent extends GenericVoiceChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "bitrate";

    public VoiceChannelUpdateBitrateEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldBitrate)
    {
        super(api, responseNumber, channel, oldBitrate, channel.getBitrate(), IDENTIFIER);
    }


    public int getOldBitrate()
    {
        return getOldValue();
    }


    public int getNewBitrate()
    {
        return getNewValue();
    }
}
