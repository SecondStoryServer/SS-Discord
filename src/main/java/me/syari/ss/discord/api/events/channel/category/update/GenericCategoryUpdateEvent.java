

package me.syari.ss.discord.api.events.channel.category.update;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.channel.category.GenericCategoryEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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
