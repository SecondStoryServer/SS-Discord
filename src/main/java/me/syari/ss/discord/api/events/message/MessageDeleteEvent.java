package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;

import javax.annotation.Nonnull;


public class MessageDeleteEvent extends GenericMessageEvent {
    public MessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel) {
        super(api, responseNumber, messageId, channel);
    }
}
