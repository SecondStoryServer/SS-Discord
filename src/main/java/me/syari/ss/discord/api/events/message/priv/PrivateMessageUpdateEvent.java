
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class PrivateMessageUpdateEvent extends GenericPrivateMessageEvent
{
    private final Message message;

    public PrivateMessageUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getPrivateChannel());
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
}
