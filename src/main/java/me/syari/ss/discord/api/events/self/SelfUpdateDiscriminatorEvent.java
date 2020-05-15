

package me.syari.ss.discord.api.events.self;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public class SelfUpdateDiscriminatorEvent extends GenericSelfUpdateEvent<String>
{
    public static final String IDENTIFIER = "discriminator";

    public SelfUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull String oldDiscriminator)
    {
        super(api, responseNumber, oldDiscriminator, api.getSelfUser().getDiscriminator(), IDENTIFIER);
    }


    @Nonnull
    public String getOldDiscriminator()
    {
        return getOldValue();
    }


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
