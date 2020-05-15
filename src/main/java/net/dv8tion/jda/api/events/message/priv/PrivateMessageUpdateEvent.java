
package net.dv8tion.jda.api.events.message.priv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was edited in a {@link net.dv8tion.jda.api.entities.PrivateChannel PrivateChannel}.
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
     * The {@link net.dv8tion.jda.api.entities.Message Message} that was updated
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
     * @see    net.dv8tion.jda.api.entities.User User
     */
    @Nonnull
    public User getAuthor()
    {
        return message.getAuthor();
    }
}
