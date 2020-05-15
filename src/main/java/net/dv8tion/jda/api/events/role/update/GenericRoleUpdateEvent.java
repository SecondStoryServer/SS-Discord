

package net.dv8tion.jda.api.events.role.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} was updated.
 * <br>Every RoleUpdateEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect any RoleUpdateEvent.
 */
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
