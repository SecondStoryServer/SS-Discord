
package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.channel.text.GenericTextChannelEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericTextChannelUpdateEvent<T> extends GenericTextChannelEvent implements UpdateEvent<TextChannel, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericTextChannelUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, channel);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public TextChannel getEntity()
    {
        return getChannel();
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return identifier;
    }

    @Nullable
    @Override
    public T getOldValue()
    {
        return previous;
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
        return "TextChannelUpdate[" + getPropertyIdentifier() + "](" +getOldValue() + "->" + getNewValue() + ')';
    }
}
