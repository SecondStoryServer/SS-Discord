

package net.dv8tion.jda.api.events.self;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the email of the current user changed. (client-only)
 *
 * <p>Can be used to retrieve the old email.
 *
 * <p>Identifier: {@code email}
 */
public class SelfUpdateEmailEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "email";

    public SelfUpdateEmailEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldEmail)
    {
        super(api, responseNumber, oldEmail, api.getSelfUser().getEmail(), IDENTIFIER);
    }

    /**
     * The old email
     *
     * @return The old email
     */
    @Nonnull
    public String getOldEmail()
    {
        return getOldValue();
    }

    /**
     * The new email
     *
     * @return The new email
     */
    @Nonnull
    public String getNewEmail()
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
