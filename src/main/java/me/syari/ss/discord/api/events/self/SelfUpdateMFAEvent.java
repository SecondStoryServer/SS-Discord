

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateMFAEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "mfa_enabled";

    public SelfUpdateMFAEvent(@Nonnull JDA api, long responseNumber, boolean wasMfaEnabled)
    {
        super(api, responseNumber, wasMfaEnabled, !wasMfaEnabled, IDENTIFIER);
    }


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
