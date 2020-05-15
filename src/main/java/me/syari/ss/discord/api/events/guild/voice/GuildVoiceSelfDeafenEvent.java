

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceSelfDeafenEvent extends GenericGuildVoiceEvent
{
    protected final boolean selfDeafened;

    public GuildVoiceSelfDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.selfDeafened = member.getVoiceState().isSelfDeafened();
    }


    public boolean isSelfDeafened()
    {
        return selfDeafened;
    }
}
