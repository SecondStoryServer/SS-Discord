
package me.syari.ss.discord.api.events.channel.text;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link TextChannel TextChannel} has been created.
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
