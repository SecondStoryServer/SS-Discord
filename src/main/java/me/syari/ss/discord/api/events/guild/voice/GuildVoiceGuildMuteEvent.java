

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceGuildMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean guildMuted;

    public GuildVoiceGuildMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.guildMuted = member.getVoiceState().isGuildMuted();
    }


    public boolean isGuildMuted()
    {
        return guildMuted;
    }
}
