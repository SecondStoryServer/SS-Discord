
package net.dv8tion.jda.api.events.channel.text.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}'s position changed.
 *
 * <p>Can be used to detect when a TextChannel position changes and get its previous position.
 *
 * <p>Identifier: {@code position}
 */
public class TextChannelUpdatePositionEvent extends GenericTextChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public TextChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, int oldPosition)
    {
        super(api, responseNumber, channel, oldPosition, channel.getPositionRaw(), IDENTIFIER);
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPosition()
    {
        return getOldValue();
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }
}
