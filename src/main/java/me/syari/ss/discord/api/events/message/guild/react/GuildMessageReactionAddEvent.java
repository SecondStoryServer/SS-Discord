package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class GuildMessageReactionAddEvent extends GenericGuildMessageReactionEvent {
    public GuildMessageReactionAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull MessageReaction reaction) {
        super(api, responseNumber, member, reaction, member.getIdLong());
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public User getUser() {
        return super.getUser();
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Member getMember() {
        return super.getMember();
    }
}
