package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class MessageBulkDeleteEvent extends Event {
    protected final TextChannel channel;
    protected final List<String> messageIds;

    public MessageBulkDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull List<String> messageIds) {
        super(api, responseNumber);
        this.channel = channel;
        this.messageIds = Collections.unmodifiableList(messageIds);
    }


    @Nonnull
    public TextChannel getChannel() {
        return channel;
    }


    @Nonnull
    public Guild getGuild() {
        return channel.getGuild();
    }


    @Nonnull
    public List<String> getMessageIds() {
        return messageIds;
    }
}
