

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateNitroEvent extends GenericSelfUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "nitro";

    public SelfUpdateNitroEvent(@Nonnull JDA api, long responseNumber, boolean wasNitro)
    {
        super(api, responseNumber, wasNitro, !wasNitro, IDENTIFIER);
    }


    public boolean wasNitro()
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
