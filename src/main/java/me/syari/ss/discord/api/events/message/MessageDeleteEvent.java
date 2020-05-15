
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a Message was deleted in a {@link MessageChannel MessageChannel}.
 * 
 * <p>Can be used to detect when a Message is deleted. No matter if private or guild.
 *
 * <p><b>JDA does not have a cache for messages and is not able to provide previous information due to limitations by the
 * Discord API!</b>
 */
public class MessageDeleteEvent extends GenericMessageEvent
{
    public MessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
