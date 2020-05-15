

package net.dv8tion.jda.api.events.channel.store.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StoreChannel;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.StoreChannel StoreChannel}'s name changed.
 *
 * <p>Can be used to detect when a StoreChannel name changes and get its previous name.
 *
 * <p>Identifier: {@code name}
 */
public class StoreChannelUpdateNameEvent extends GenericStoreChannelUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public StoreChannelUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel,
                                       @Nonnull String prev)
    {
        super(api, responseNumber, channel, prev, channel.getName(), IDENTIFIER);
    }

    /**
     * The old name
     *
     * @return The old name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name
     *
     * @return The new name
     */
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
