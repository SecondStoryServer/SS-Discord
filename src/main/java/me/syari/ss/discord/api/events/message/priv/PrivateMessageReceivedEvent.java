
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was sent in a {@link PrivateChannel PrivateChannel}.
 * 
 * <p>Can be used to retrieve affected PrivateChannel and Message.
 */
public class PrivateMessageReceivedEvent extends GenericPrivateMessageEvent
{
    private final Message message;

    public PrivateMessageReceivedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Message message)
    {
        super(api, responseNumber, message.getIdLong(), message.getPrivateChannel());
        this.message = message;
    }

    /**
     * The {@link Message Message}
     *
     * @return The Message
     */
    @Nonnull
    public Message getMessage()
    {
        return message;
    }

    /**
     * The author for this message
     *
     * @return The author for this message
     *
     * @see    User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }
}
