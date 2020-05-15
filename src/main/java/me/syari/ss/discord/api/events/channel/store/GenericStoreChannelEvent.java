

package me.syari.ss.discord.api.events.channel.store;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link StoreChannel StoreChannel} event was fired.
 * <br>Every StoreChannelEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any StoreChannelEvent.
 */
public abstract class GenericStoreChannelEvent extends Event
{
    protected final StoreChannel channel;

    public GenericStoreChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel)
    {
        super(api, responseNumber);
        this.channel = channel;
    }

    /**
     * The {@link StoreChannel}.
     *
     * @return The channel
     */
    @Nonnull
    public StoreChannel getChannel()
    {
        return channel;
    }
}
