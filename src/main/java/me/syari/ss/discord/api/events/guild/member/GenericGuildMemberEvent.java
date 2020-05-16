package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;

import javax.annotation.Nonnull;


public abstract class GenericGuildMemberEvent extends GenericGuildEvent {
    private final Member member;

    public GenericGuildMemberEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member) {
        super(api, responseNumber, member.getGuild());
        this.member = member;
    }


    @Nonnull
    public User getUser() {
        return getMember().getUser();
    }


    @Nonnull
    public Member getMember() {
        return member;
    }
}
