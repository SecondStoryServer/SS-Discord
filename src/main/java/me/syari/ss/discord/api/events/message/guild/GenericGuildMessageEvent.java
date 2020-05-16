package me.syari.ss.discord.api.events.message.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;

import javax.annotation.Nonnull;


public abstract class GenericGuildMessageEvent extends GenericGuildEvent {
    protected final long messageId;
    protected final TextChannel channel;

    public GenericGuildMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull TextChannel channel) {
        super(api, responseNumber, channel.getGuild());
        this.messageId = messageId;
        this.channel = channel;
    }


    @Nonnull
    public String getMessageId() {
        return Long.toUnsignedString(messageId);
    }


    public long getMessageIdLong() {
        return messageId;
    }


    @Nonnull
    public TextChannel getChannel() {
        return channel;
    }
}
