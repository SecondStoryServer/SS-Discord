

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceGuildDeafenEvent extends GenericGuildVoiceEvent
{
    protected final boolean guildDeafened;

    public GuildVoiceGuildDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.guildDeafened = member.getVoiceState().isGuildDeafened();
    }


    public boolean isGuildDeafened()
    {
        return guildDeafened;
    }
}
