package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceDeafenEvent extends GenericGuildVoiceEvent {
    protected final boolean deafened;

    public GuildVoiceDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member) {
        super(api, responseNumber, member);
        this.deafened = member.getVoiceState().isDeafened();
    }


    public boolean isDeafened() {
        return deafened;
    }
}
