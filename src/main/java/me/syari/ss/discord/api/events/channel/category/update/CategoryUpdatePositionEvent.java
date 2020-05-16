package me.syari.ss.discord.api.events.channel.category.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;

import javax.annotation.Nonnull;


public class CategoryUpdatePositionEvent extends GenericCategoryUpdateEvent<Integer> {
    public static final String IDENTIFIER = "position";

    public CategoryUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Category category, int oldPosition) {
        super(api, responseNumber, category, oldPosition, category.getPositionRaw(), IDENTIFIER);
    }


    public int getOldPosition() {
        return getOldValue();
    }


    public int getNewPosition() {
        return getNewValue();
    }
}
