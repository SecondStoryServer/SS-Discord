
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class GuildBanEvent extends GenericGuildEvent
{
    private final User user;

    public GuildBanEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull User user)
    {
        super(api, responseNumber, guild);
        this.user = user;
    }

    
    @Nonnull
    public User getUser()
    {
        return user;
    }
}
