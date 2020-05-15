

package me.syari.ss.discord.api.events.channel.category;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Category Category} was created.
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
