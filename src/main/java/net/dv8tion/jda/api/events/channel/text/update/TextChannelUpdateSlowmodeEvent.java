
package net.dv8tion.jda.api.events.channel.text.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}'s slowmode changed.
 *
 * <p>Can be used to detect when a TextChannel slowmode changes and get its previous value.
 *
 * <p>Identifier: {@code slowmode}
 */
public class TextChannelUpdateSlowmodeEvent extends GenericTextChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "slowmode";

    public TextChannelUpdateSlowmodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, int oldSlowmode)
    {
        super(api, responseNumber, channel, oldSlowmode, channel.getSlowmode(), IDENTIFIER);
    }

    /**
     * The old slowmode.
     *
     * @return The old slowmode.
     */
    public int getOldSlowmode()
    {
        return getOldValue();
    }

    /**
     * The new slowmode.
     *
     * @return The new slowmode.
     */
    public int getNewSlowmode()
    {
        return getNewValue();
    }
}
