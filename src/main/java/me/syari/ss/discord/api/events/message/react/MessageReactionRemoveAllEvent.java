

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.events.message.GenericMessageEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;

import javax.annotation.Nonnull;

/**
 * Indicates the the reactions of a message have been cleared by a moderator
 *
 * <p>Can be used to detect when the reactions of a message are removed by a moderator
 */
public class MessageReactionRemoveAllEvent extends GenericMessageEvent
{
    public MessageReactionRemoveAllEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
