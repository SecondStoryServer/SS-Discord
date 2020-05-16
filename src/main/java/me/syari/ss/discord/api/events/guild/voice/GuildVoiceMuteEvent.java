

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean muted;

    public GuildVoiceMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.muted = member.getVoiceState().isMuted();
    }


    public boolean isMuted()
    {
        return muted;
    }
}
