

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GuildUpdateAfkChannelEvent extends GenericGuildUpdateEvent<VoiceChannel>
{
    public static final String IDENTIFIER = "afk_channel";

    public GuildUpdateAfkChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable VoiceChannel oldAfkChannel)
    {
        super(api, responseNumber, guild, oldAfkChannel, guild.getAfkChannel(), IDENTIFIER);
    }


    @Nullable
    public VoiceChannel getOldAfkChannel()
    {
        return getOldValue();
    }


    @Nullable
    public VoiceChannel getNewAfkChannel()
    {
        return getNewValue();
    }
}
