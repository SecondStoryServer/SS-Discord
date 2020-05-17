package me.syari.ss.discord.api.event;

import me.syari.ss.discord.internal.entities.Member;
import me.syari.ss.discord.internal.entities.Message;
import me.syari.ss.discord.internal.entities.TextChannel;
import me.syari.ss.discord.internal.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageReceivedEvent {
    protected final TextChannel channel;
    private final Message message;

    public MessageReceivedEvent(@NotNull Message message) {
        this.channel = message.getChannel();
        this.message = message;
    }

    @NotNull
    public Message getMessage() {
        return message;
    }


    @NotNull
    public User getAuthor() {
        return message.getAuthor();
    }


    @Nullable
    public Member getMember() {
        return message.getMember();
    }

    @NotNull
    public TextChannel getChannel() {
        return channel;
    }
}
