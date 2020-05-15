

package me.syari.ss.discord.api.events.channel.category;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericCategoryEvent extends Event
{
    protected final Category category;

    public GenericCategoryEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category)
    {
        super(api, responseNumber);
        this.category = category;
    }


    @Nonnull
    public Category getCategory()
    {
        return category;
    }


    @Nonnull
    public String getId()
    {
        return Long.toUnsignedString(getIdLong());
    }


    public long getIdLong()
    {
        return category.getIdLong();
    }


    @Nonnull
    public Guild getGuild()
    {
        return category.getGuild();
    }
}
