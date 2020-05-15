

package net.dv8tion.jda.api.events.message.react;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

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
