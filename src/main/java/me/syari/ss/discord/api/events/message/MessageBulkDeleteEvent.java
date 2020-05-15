
package me.syari.ss.discord.api.events.message;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that a bulk deletion is executed in a {@link TextChannel TextChannel}.
 * <br>Set {@link JDABuilder#setBulkDeleteSplittingEnabled(boolean)} to false in order to enable this event.
 * 
 * <p>Can be used to detect that a large chunk of Messages is deleted in a TextChannel. Providing a list of Message IDs and the specific TextChannel.
 */
public class MessageBulkDeleteEvent extends Event
{
    protected final TextChannel channel;
    protected final List<String> messageIds;

    public MessageBulkDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nonnull List<String> messageIds)
    {
        super(api, responseNumber);
        this.channel = channel;
        this.messageIds = Collections.unmodifiableList(messageIds);
    }

    /**
     * The {@link TextChannel TextChannel} where the messages have been deleted
     *
     * @return The TextChannel
     */
    @Nonnull
    public TextChannel getChannel()
    {
        return channel;
    }

    /**
     * The {@link Guild Guild} where the messages were deleted.
     *
     * @return The Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return channel.getGuild();
    }
    
    /**
     * List of messages that have been deleted.
     *
     * @return The list of message ids
     */
    @Nonnull
    public List<String> getMessageIds()
    {
        return messageIds;
    }
}
