

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateNameEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public SelfUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldName)
    {
        super(api, responseNumber, oldName, api.getSelfUser().getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }


    @Nonnull
    public String getNewName()
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
