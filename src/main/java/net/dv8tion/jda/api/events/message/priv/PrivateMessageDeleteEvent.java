
package net.dv8tion.jda.api.events.message.priv;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was deleted in a {@link net.dv8tion.jda.api.entities.PrivateChannel PrivateChannel}.
 * 
 * <p>Can be used to retrieve affected PrivateChannel and the ID of the deleted Message.
 */
public class PrivateMessageDeleteEvent extends GenericPrivateMessageEvent
{
    public PrivateMessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull PrivateChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
