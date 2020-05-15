

package net.dv8tion.jda.api.events.channel.category;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Category Category} was created.
 *
 * <p>Can be used to retrieve the newly created Category
 */
public class CategoryCreateEvent extends GenericCategoryEvent
{
    public CategoryCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category)
    {
        super(api, responseNumber, category);
    }
}
