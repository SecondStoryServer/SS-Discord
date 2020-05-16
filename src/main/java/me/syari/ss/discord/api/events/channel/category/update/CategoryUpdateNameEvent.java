

package me.syari.ss.discord.api.events.channel.category.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;


public class CategoryUpdateNameEvent extends GenericCategoryUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public CategoryUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, @Nonnull String oldName)
    {
        super(api, responseNumber, category, oldName, category.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }


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
