
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was edited in a {@link PrivateChannel PrivateChannel}.
 * 
 * <p>Can be used retrieve affected PrivateChannel and edited Message.
 */
public class PrivateMessageUpdateEvent extends GenericPrivateMessageEvent
{
    private final Message message;

    public PrivateMessageUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getPrivateChannel());
        this.message = message;
    }

    /**
     * The {@link Message Message} that was updated
     *
     * @return The Message
     */
    @Nonnull
    public Message getMessage()
    {
        return message;
    }

    /**
     * The author of this message
     *
     * @return The author of this message
     *
     * @see    User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }
}
