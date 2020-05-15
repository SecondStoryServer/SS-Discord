

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.SelfUser;
import me.syari.ss.discord.api.events.Event;
import me.syari.ss.discord.api.events.UpdateEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericSelfUpdateEvent<T> extends Event implements UpdateEvent<SelfUser, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericSelfUpdateEvent(
            @Nonnull JDA api, long responseNumber,
            @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }


    @Nonnull
    public SelfUser getSelfUser()
    {
        return api.getSelfUser();
    }

    @Nonnull
    @Override
    public SelfUser getEntity()
    {
        return getSelfUser();
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
        return "SelfUserUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
