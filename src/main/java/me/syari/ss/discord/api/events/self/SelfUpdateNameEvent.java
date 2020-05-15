

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the name of the current user changed.
 *
 * <p>Can be used to retrieve the old name.
 *
 * <p>Identifier: {@code name}
 */
public class SelfUpdateNameEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public SelfUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldName)
    {
        super(api, responseNumber, oldName, api.getSelfUser().getName(), IDENTIFIER);
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
