

package net.dv8tion.jda.api.events.channel.text.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}'s parent changed.
 *
 * <p>Can be used to detect that the parent of a TextChannel changes.
 *
 * <p>Identifier: {@code parent}
 */
public class TextChannelUpdateParentEvent extends GenericTextChannelUpdateEvent<Category>
{
    public static final String IDENTIFIER = "parent";

    public TextChannelUpdateParentEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nullable Category oldParent)
    {
        super(api, responseNumber, channel, oldParent, channel.getParent(), IDENTIFIER);
    }

    /**
     * The old parent {@link net.dv8tion.jda.api.entities.Category Category}
     *
     * @return The old parent category, or null
     */
    @Nullable
    public Category getOldParent()
    {
        return getOldValue();
    }

    /**
     * The new parent {@link net.dv8tion.jda.api.entities.Category Category}
     *
     * @return The new parent category, or null
     */
    @Nullable
    public Category getNewParent()
    {
        return getNewValue();
    }
}
