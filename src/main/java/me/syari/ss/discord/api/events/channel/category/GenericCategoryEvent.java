

package me.syari.ss.discord.api.events.channel.category;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Category Category} was created/deleted/updated.
 * <br>Every category event is a subclass of this event and can be casted
 *
 * <p>Can be used to detect that any category event was fired
 */
public abstract class GenericCategoryEvent extends Event
{
    protected final Category category;

    public GenericCategoryEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category)
    {
        super(api, responseNumber);
        this.category = category;
    }

    /**
     * The responsible {@link Category Category}
     *
     * @return The Category
     */
    @Nonnull
    public Category getCategory()
    {
        return category;
    }

    /**
     * The snowflake ID for the responsible {@link Category Category}
     *
     * @return The ID for the category
     */
    @Nonnull
    public String getId()
    {
        return Long.toUnsignedString(getIdLong());
    }

    /**
     * The snowflake ID for the responsible {@link Category Category}
     *
     * @return The ID for the category
     */
    public long getIdLong()
    {
        return category.getIdLong();
    }

    /**
     * The {@link Guild Guild}
     * the responsible {@link Category Category} is part of.
     *
     * @return The {@link Guild Guild}
     */
    @Nonnull
    public Guild getGuild()
    {
        return category.getGuild();
    }
}
