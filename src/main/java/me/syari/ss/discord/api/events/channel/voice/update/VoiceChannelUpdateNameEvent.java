
package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class VoiceChannelUpdateNameEvent extends GenericVoiceChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public VoiceChannelUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, @Nonnull String oldName)
    {
        super(api, responseNumber, channel, oldName, channel.getName(), IDENTIFIER);
    }

    
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
