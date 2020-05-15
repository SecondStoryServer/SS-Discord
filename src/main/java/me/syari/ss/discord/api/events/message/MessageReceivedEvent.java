
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MessageReceivedEvent extends GenericMessageEvent
{
    private final Message message;

    public MessageReceivedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getChannel());
        this.message = message;
    }


    @Nonnull
    public Message getMessage()
    {
        return message;
    }


    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }


    @Nullable
    public Member getMember()
    {
        return message.getMember();
    }


    public boolean isWebhookMessage()
    {
        return getMessage().isWebhookMessage();
    }
}
