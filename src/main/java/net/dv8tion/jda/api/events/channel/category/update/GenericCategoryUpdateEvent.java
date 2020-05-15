

package net.dv8tion.jda.api.events.channel.category.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.channel.category.GenericCategoryEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Category Category} was updated.
 * <br>Every category update event derived from this event and can be casted.
 *
 * <p>Can be used to detect any category update event
 */
public abstract class GenericCategoryUpdateEvent<T> extends GenericCategoryEvent implements UpdateEvent<Category, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericCategoryUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull Category category,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, category);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public Category getEntity()
    {
        return getCategory();
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
        return "CategoryUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
