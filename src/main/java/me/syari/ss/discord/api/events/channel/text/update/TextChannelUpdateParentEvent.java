

package me.syari.ss.discord.api.events.channel.text.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.entities.TextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class TextChannelUpdateParentEvent extends GenericTextChannelUpdateEvent<Category>
{
    public static final String IDENTIFIER = "parent";

    public TextChannelUpdateParentEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel, @Nullable Category oldParent)
    {
        super(api, responseNumber, channel, oldParent, channel.getParent(), IDENTIFIER);
    }


    @Nullable
    public Category getOldParent()
    {
        return getOldValue();
    }


    @Nullable
    public Category getNewParent()
    {
        return getNewValue();
    }
}
