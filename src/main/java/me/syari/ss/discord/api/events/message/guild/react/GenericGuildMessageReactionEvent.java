package me.syari.ss.discord.api.events.message.guild.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.message.guild.GenericGuildMessageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericGuildMessageReactionEvent extends GenericGuildMessageEvent {
    protected final long userId;
    protected final Member issuer;
    protected final MessageReaction reaction;

    public GenericGuildMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nullable Member user, @Nonnull MessageReaction reaction, long userId) {
        super(api, responseNumber, reaction.getMessageIdLong(), (TextChannel) reaction.getChannel());
        this.issuer = user;
        this.reaction = reaction;
        this.userId = userId;
    }


    @Nonnull
    public String getUserId() {
        return Long.toUnsignedString(userId);
    }


    public long getUserIdLong() {
        return userId;
    }


    @Nullable
    public User getUser() {
        return issuer == null ? getJDA().getUserById(userId) : issuer.getUser();
    }


    @Nullable
    public Member getMember() {
        return issuer;
    }


    @Nonnull
    public MessageReaction getReaction() {
        return reaction;
    }


    @Nonnull
    public MessageReaction.ReactionEmote getReactionEmote() {
        return reaction.getReactionEmote();
    }
}
