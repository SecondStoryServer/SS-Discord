

package net.dv8tion.jda.api.events.channel.store;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel} has been deleted.
 *
 * <p>Can be used to detect when a StoreChannel has been deleted.
 */
public class StoreChannelDeleteEvent extends GenericStoreChannelEvent
{
    public StoreChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
