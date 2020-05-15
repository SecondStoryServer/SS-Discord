
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;

import javax.annotation.Nonnull;


public class PrivateMessageDeleteEvent extends GenericPrivateMessageEvent
{
    public PrivateMessageDeleteEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull PrivateChannel channel)
    {
        super(api, responseNumber, messageId, channel);
    }
}
