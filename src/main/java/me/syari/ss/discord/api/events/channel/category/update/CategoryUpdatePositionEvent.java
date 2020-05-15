

package me.syari.ss.discord.api.events.channel.category.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;

/**
 * Indicates that the position of a {@link Category Category} was updated.
 *
 * <p>Can be used to retrieve the old position
 *
 * <p>Identifier: {@code position}
 */
public class CategoryUpdatePositionEvent extends GenericCategoryUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    public CategoryUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, int oldPosition)
    {
        super(api, responseNumber, category, oldPosition, category.getPositionRaw(), IDENTIFIER);
    }

    /**
     * The previous position of this {@link Category Category}
     *
     * @return The previous position
     */
    public int getOldPosition()
    {
        return getOldValue();
    }

    /**
     * The new position of this {@link Category Category}
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }
}
