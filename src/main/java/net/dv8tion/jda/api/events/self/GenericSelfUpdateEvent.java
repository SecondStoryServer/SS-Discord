

package net.dv8tion.jda.api.events.self;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.UpdateEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.SelfUser SelfUser} changed or started an activity.
 * <br>Every SelfUserEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect any SelfUserEvent.
 */
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

    /**
     * The {@link net.dv8tion.jda.api.entities.SelfUser SelfUser}
     *
     * @return The {@link net.dv8tion.jda.api.entities.SelfUser SelfUser}
     */
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
