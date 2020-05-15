

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the verification state of the current user changed. (client-only)
 *
 * <p>Can be used to retrieve the old verification state.
 *
 * <p>Identifier: {@code verified}
 */
public class SelfUpdateVerifiedEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "verified";

    public SelfUpdateVerifiedEvent(@Nonnull JDA api, long responseNumber, boolean wasVerified)
    {
        super(api, responseNumber, wasVerified, !wasVerified, IDENTIFIER);
    }

    /**
     * Whether the account was verified
     *
     * @return True, if this account was previously verified
     */
    public boolean wasVerified()
    {
        return getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getNewValue()
    {
        return super.getNewValue();
    }
}
