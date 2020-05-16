
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class TextChannelUpdateTopicEvent extends GenericTextChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "topic";

    public TextChannelUpdateTopicEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nullable String oldTopic)
    {
        super(api, responseNumber, channel, oldTopic, channel.getTopic(), IDENTIFIER);
    }


    @Nullable
    public String getOldTopic()
    {
        return getOldValue();
    }


    @Nullable
    public String getNewTopic()
    {
        return getNewValue();
    }
}
