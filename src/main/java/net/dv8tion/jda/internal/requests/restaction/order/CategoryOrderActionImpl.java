

package net.dv8tion.jda.internal.requests.restaction.order;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

public class CategoryOrderActionImpl
    extends ChannelOrderActionImpl
    implements CategoryOrderAction
{
    protected final Category category;

    /**
     * Creates a new CategoryOrderAction for the specified {@link net.dv8tion.jda.api.entities.Category Category}
     *
     * @param  category
     *         The target {@link net.dv8tion.jda.api.entities.Category Category}
     *         which the new CategoryOrderAction will order channels from.
     * @param  bucket
     *         The sorting bucket
     */
    public CategoryOrderActionImpl(Category category, int bucket)
    {
        super(category.getGuild(), bucket, getChannelsOfType(category, bucket));
        this.category = category;
    }

    @Nonnull
    @Override
    public Category getCategory()
    {
        return category;
    }

    @Override
    protected void validateInput(GuildChannel entity)
    {
        Checks.notNull(entity, "Provided channel");
        Checks.check(getCategory().equals(entity.getParent()), "Provided channel's Category is not this Category!");
        Checks.check(orderList.contains(entity), "Provided channel is not in the list of orderable channels!");
    }

    @Nonnull
    private static Collection<GuildChannel> getChannelsOfType(Category category, int bucket)
    {
        Checks.notNull(category, "Category");
        return ChannelOrderActionImpl.getChannelsOfType(category.getGuild(), bucket).stream()
             .filter(it -> category.equals(it.getParent()))
             .sorted()
             .collect(Collectors.toList());
    }
}
