
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link TextChannel TextChannel}'s topic changed.
 *
 * <p>Can be used to detect when a TextChannel topic changes and get its previous topic.
 *
 * <p>Identifier: {@code topic}
 */
public class TextChannelUpdateTopicEvent extends GenericTextChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "topic";

    public TextChannelUpdateTopicEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nullable String oldTopic)
    {
        super(api, responseNumber, channel, oldTopic, channel.getTopic(), IDENTIFIER);
    }

    /**
     * The old topic
     *
     * @return The old topic, or null
     */
    @Nullable
    public String getOldTopic()
    {
        return getOldValue();
    }

    /**
     * The new topic
     *
     * @return The new topic, or null
     */
    @Nullable
    public String getNewTopic()
    {
        return getNewValue();
    }
}
