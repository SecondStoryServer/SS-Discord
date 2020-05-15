

package net.dv8tion.jda.api.events.channel.store.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel}'s position changed.
 *
 * <p>Can be used to detect when a StoreChannel position changes and get its previous position.
 *
 * <p>Identifier: {@code position}
 */
public class StoreChannelUpdatePositionEvent extends GenericStoreChannelUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public StoreChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, int prev)
    {
        super(api, responseNumber, channel, prev, channel.getPositionRaw(), IDENTIFIER);
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
