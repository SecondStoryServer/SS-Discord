

package me.syari.ss.discord.api.events.user.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class UserUpdateDiscriminatorEvent extends GenericUserUpdateEvent<String>
{
    public static final String IDENTIFIER = "discriminator";

    public UserUpdateDiscriminatorEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nonnull String oldDiscriminator)
    {
        super(api, responseNumber, user, oldDiscriminator, user.getDiscriminator(), IDENTIFIER);
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
