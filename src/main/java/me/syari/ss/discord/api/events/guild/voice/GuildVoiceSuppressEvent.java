package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;


public class GuildVoiceSuppressEvent extends GenericGuildVoiceEvent {
    protected final boolean suppressed;

    public GuildVoiceSuppressEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member) {
        super(api, responseNumber, member);
        this.suppressed = member.getVoiceState().isSuppressed();
    }


    public boolean isSuppressed() {
        return suppressed;
    }
}
