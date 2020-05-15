

package net.dv8tion.jda.api.events.channel.store;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel} has been created.
 *
 * <p>Can be used to detect new StoreChannel creation.
 */
public class StoreChannelCreateEvent extends GenericStoreChannelEvent
{
    public StoreChannelCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
