package me.syari.ss.discord.api.events.channel.category;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;


public class CategoryDeleteEvent extends GenericCategoryEvent {
    public CategoryDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category) {
        super(api, responseNumber, category);
    }
}
