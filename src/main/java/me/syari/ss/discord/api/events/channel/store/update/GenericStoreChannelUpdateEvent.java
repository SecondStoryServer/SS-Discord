

package me.syari.ss.discord.api.events.channel.store.update;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.channel.store.GenericStoreChannelEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericStoreChannelUpdateEvent<T> extends GenericStoreChannelEvent implements UpdateEvent<StoreChannel, T>
{
    protected final T prev;
    protected final T next;
    protected final String identifier;

    public GenericStoreChannelUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel,
                                          @Nullable T prev, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, channel);
        this.prev = prev;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return identifier;
    }

    @Nonnull
    @Override
    public StoreChannel getEntity()
    {
        return channel;
    }

    @Nullable
    @Override
    public T getOldValue()
    {
        return prev;
    }

    @Nullable
    @Override
    public T getNewValue()
    {
        return next;
    }

    @Override
    public String toString()
    {
        return "StoreChannelUpdate[" + getPropertyIdentifier() + "](" +getOldValue() + "->" + getNewValue() + ')';
    }
}
