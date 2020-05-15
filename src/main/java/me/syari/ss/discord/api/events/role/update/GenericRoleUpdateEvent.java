

package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.role.GenericRoleEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericRoleUpdateEvent<T> extends GenericRoleEvent implements UpdateEvent<Role, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericRoleUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull Role role,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, role);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public Role getEntity()
    {
        return role;
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
        return "RoleUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ")";
    }
}
