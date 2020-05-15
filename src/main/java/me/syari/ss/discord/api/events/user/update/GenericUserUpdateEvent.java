

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.user.GenericUserEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GenericUserUpdateEvent<T> extends GenericUserEvent implements UpdateEvent<User, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericUserUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull User user,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, user);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public User getEntity()
    {
        return getUser();
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
        return "UserUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
