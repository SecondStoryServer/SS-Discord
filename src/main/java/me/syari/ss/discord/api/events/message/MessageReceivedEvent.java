package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MessageReceivedEvent implements GenericEvent {
    protected long messageId;
    protected MessageChannel channel;
    private final Message message;

    public MessageReceivedEvent(@Nonnull Message message) {
        this.messageId = message.getIdLong();
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
    public MessageChannel getChannel() {
        return channel;
    }
}
