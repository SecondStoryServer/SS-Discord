
package me.syari.ss.discord.api.events.user;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericUserEvent extends Event
{
    private final User user;

    public GenericUserEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user)
    {
        super(api, responseNumber);
        this.user = user;
    }


    @Nonnull
    public User getUser()
    {
        return user;
    }
}
