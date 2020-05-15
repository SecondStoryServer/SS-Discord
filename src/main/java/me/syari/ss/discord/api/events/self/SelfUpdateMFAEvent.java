

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that the mfa level of the current user changed.
 * <br>This is relevant for elevated permissions (guild moderating/managing).
 *
 * <p>Can be used to retrieve the old mfa level.
 *
 * <p>Identifier: {@code mfa_enabled}
 */
public class SelfUpdateMFAEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "mfa_enabled";

    public SelfUpdateMFAEvent(@Nonnull JDA api, long responseNumber, boolean wasMfaEnabled)
    {
        super(api, responseNumber, wasMfaEnabled, !wasMfaEnabled, IDENTIFIER);
    }

    /**
     * Whether MFA was previously enabled or not
     *
     * @return True, if the account had MFA enabled prior to this event
     */
    public boolean wasMfaEnabled()
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
