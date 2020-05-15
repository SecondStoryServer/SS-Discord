

package net.dv8tion.jda.api.events.channel.store;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel} event was fired.
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
     * The {@link net.dv8tion.jda.api.entities.StoreChannel}.
     *
     * @return The channel
     */
    @Nonnull
    public StoreChannel getChannel()
    {
        return channel;
    }
}
