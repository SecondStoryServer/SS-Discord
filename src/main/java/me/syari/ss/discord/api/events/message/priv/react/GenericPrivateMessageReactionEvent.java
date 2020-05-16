package me.syari.ss.discord.api.events.message.priv.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.message.priv.GenericPrivateMessageEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GenericPrivateMessageReactionEvent extends GenericPrivateMessageEvent {
    protected final long userId;
    protected final User issuer;
    protected final MessageReaction reaction;

    public GenericPrivateMessageReactionEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull MessageReaction reaction, long userId) {
        super(api, responseNumber, reaction.getMessageIdLong(), (PrivateChannel) reaction.getChannel());
        this.userId = userId;
        this.issuer = user;
        this.reaction = reaction;
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
