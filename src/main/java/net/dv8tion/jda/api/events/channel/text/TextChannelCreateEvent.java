
package net.dv8tion.jda.api.events.channel.text;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} has been created.
 *
 * <p>Can be used to detect new TextChannel creation.
 */
public class TextChannelCreateEvent extends GenericTextChannelEvent
{
    public TextChannelCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
