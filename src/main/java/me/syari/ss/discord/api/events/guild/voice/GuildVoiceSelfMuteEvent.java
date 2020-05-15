

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceSelfMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean selfMuted;

    public GuildVoiceSelfMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.selfMuted = member.getVoiceState().isSelfMuted();
    }

    
    public boolean isSelfMuted()
    {
        return selfMuted;
    }
}
