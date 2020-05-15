

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateEmailEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "email";

    public SelfUpdateEmailEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldEmail)
    {
        super(api, responseNumber, oldEmail, api.getSelfUser().getEmail(), IDENTIFIER);
    }


    @Nonnull
    public String getOldEmail()
    {
        return getOldValue();
    }


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
