

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class SelfUpdatePhoneNumberEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "phone";

    public SelfUpdatePhoneNumberEvent(@Nonnull JDA api, long responseNumber, @Nullable String oldPhoneNumber)
    {
        super(api, responseNumber, oldPhoneNumber, api.getSelfUser().getPhoneNumber(), IDENTIFIER);
    }


    @Nullable
    public String getOldPhoneNumber()
    {
        return getOldValue();
    }


    @Nullable
    public String getNewPhoneNumber()
    {
        return getNewValue();
    }
}
