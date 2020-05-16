package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;

import javax.annotation.Nonnull;


public abstract class GenericGuildVoiceEvent extends GenericGuildEvent {
    protected final Member member;

    public GenericGuildVoiceEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member) {
        super(api, responseNumber, member.getGuild());
        this.member = member;
    }


    @Nonnull
    public Member getMember() {
        return member;
    }


    @Nonnull
    public GuildVoiceState getVoiceState() {
        return member.getVoiceState();
    }
}
