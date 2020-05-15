
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was deleted in a {@link PrivateChannel PrivateChannel}.
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
