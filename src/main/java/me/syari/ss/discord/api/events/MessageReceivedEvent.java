package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.internal.entities.Member;
import me.syari.ss.discord.internal.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MessageReceivedEvent {
    protected final TextChannel channel;
    private final Message message;

    public MessageReceivedEvent(@Nonnull Message message) {
        this.channel = message.getChannel();
        this.message = message;
    }

    @Nonnull
    public Message getMessage() {
        return message;
    }


    @Nonnull
    public User getAuthor() {
        return message.getAuthor();
    }


    @Nullable
    public Member getMember() {
        return message.getMember();
    }

    @Nonnull
    public TextChannel getChannel() {
        return channel;
    }
}
