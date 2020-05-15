

package me.syari.ss.discord.api.events.message.react;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.MessageChannel;
import me.syari.ss.discord.api.events.message.GenericMessageEvent;

import javax.annotation.Nonnull;


public class MessageReactionRemoveAllEvent extends GenericMessageEvent
{
    public MessageReactionRemoveAllEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull MessageChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
