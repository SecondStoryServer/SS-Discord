
package me.syari.ss.discord.api.events.message.priv;

import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Message Message} event is fired from a {@link PrivateChannel PrivateChannel}.
 * <br>Every PrivateMessageEvent is an instance of this event and can be casted.
 * 
 * <p>Can be used to detect any PrivateMessageEvent.
 */
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

    /**
     * The {@link PrivateChannel PrivateChannel} for the message
     *
     * @return The PrivateChannel
     */
    @Nonnull
    public PrivateChannel getChannel()
    {
        return channel;
    }

    /**
     * The id for this message
     *
     * @return The id for this message
     */
    @Nonnull
    public String getMessageId()
    {
        return Long.toUnsignedString(messageId);
    }

    /**
     * The id for this message
     *
     * @return The id for this message
     */
    public long getMessageIdLong()
    {
        return messageId;
    }
}
