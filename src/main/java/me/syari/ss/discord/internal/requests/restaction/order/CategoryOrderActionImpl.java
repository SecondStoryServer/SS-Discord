package me.syari.ss.discord.internal.requests.restaction.order;

import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.api.requests.restaction.order.CategoryOrderAction;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

public class CategoryOrderActionImpl
        extends ChannelOrderActionImpl
        implements CategoryOrderAction {
    protected final Category category;


    public CategoryOrderActionImpl(Category category, int bucket) {
        super(category.getGuild(), bucket, getChannelsOfType(category, bucket));
        this.category = category;
    }

    @Nonnull
    @Override
    public Category getCategory() {
        return category;
    }

    @Nonnull
    private static Collection<GuildChannel> getChannelsOfType(Category category, int bucket) {
        Checks.notNull(category, "Category");
        return getChannelsOfType(category.getGuild(), bucket).stream()
                .filter(it -> category.equals(it.getParent()))
                .sorted()
                .collect(Collectors.toList());
    }
}
