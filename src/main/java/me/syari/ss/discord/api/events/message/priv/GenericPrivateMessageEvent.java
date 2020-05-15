
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericPrivateMessageEvent extends Event
{
    protected final long messageId;
    protected final PrivateChannel channel;

    public GenericPrivateMessageEvent(@Nonnull JDA api, long responseNumber, long messageId, @Nonnull PrivateChannel channel)
    {
        super(api, responseNumber);
        this.messageId = messageId;
        this.channel = channel;
    }

    
    @Nonnull
    public PrivateChannel getChannel()
    {
        return channel;
    }

    
    @Nonnull
    public String getMessageId()
    {
        return Long.toUnsignedString(messageId);
    }

    
    public long getMessageIdLong()
    {
        return messageId;
    }
}
