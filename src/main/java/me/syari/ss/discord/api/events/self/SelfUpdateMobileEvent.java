

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateMobileEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "mobile";

    public SelfUpdateMobileEvent(@Nonnull JDA api, long responseNumber, boolean wasMobile)
    {
        super(api, responseNumber, wasMobile, !wasMobile, IDENTIFIER);
    }


    public boolean wasMobile()
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
