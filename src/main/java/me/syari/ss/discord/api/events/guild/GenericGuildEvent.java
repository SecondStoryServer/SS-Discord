
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericGuildEvent extends Event
{
    protected final Guild guild;

    public GenericGuildEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber);
        this.guild = guild;
    }


    @Nonnull
    public Guild getGuild()
    {
        return guild;
    }
}
