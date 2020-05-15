

package net.dv8tion.jda.api.events.user.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that the discriminator of a {@link net.dv8tion.jda.api.entities.User User} changed.
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve the User who changed their discriminator and their previous discriminator.
 *
 * <p>Identifier: {@code discriminator}
 */
public class UserUpdateDiscriminatorEvent extends GenericUserUpdateEvent<String>
{
    public static final String IDENTIFIER = "discriminator";

    public UserUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldDiscriminator)
    {
        super(api, responseNumber, user, oldDiscriminator, user.getDiscriminator(), IDENTIFIER);
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
