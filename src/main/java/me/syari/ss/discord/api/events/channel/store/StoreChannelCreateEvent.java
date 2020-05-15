

package me.syari.ss.discord.api.events.channel.store;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link StoreChannel StoreChannel} has been created.
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
