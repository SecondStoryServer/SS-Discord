

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the discriminator of the current user changed.
 *
 * <p>Can be used to retrieve the old discriminator.
 *
 * <p>Identifier: {@code discriminator}
 */
public class SelfUpdateDiscriminatorEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "discriminator";

    public SelfUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldDiscriminator)
    {
        super(api, responseNumber, oldDiscriminator, api.getSelfUser().getDiscriminator(), IDENTIFIER);
    }

    /**
     * The old discriminator
     *
     * @return The old discriminator
     */
    @Nonnull
    public String getOldDiscriminator()
    {
        return getOldValue();
    }

    /**
     * The new discriminator
     *
     * @return The new discriminator
     */
    @Nonnull
    public String getNewDiscriminator()
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
