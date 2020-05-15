

package net.dv8tion.jda.api.events.channel.category.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;

import javax.annotation.Nonnull;

/**
 * Indicates that the name of a {@link net.dv8tion.jda.api.entities.Category Category} was updated.
 *
 * <p>Can be used to retrieve the old name
 *
 * <p>Identifier: {@code name}
 */
public class CategoryUpdateNameEvent extends GenericCategoryUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public CategoryUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, @Nonnull String oldName)
    {
        super(api, responseNumber, category, oldName, category.getName(), IDENTIFIER);
    }

    /**
     * The previous name for this {@link net.dv8tion.jda.api.entities.Category Category}
     *
     * @return The previous name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name for this {@link net.dv8tion.jda.api.entities.Category Category}
     *
     * @return The new name
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
