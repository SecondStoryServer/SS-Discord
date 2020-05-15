
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildJoinEvent extends GenericGuildEvent
{
    public GuildJoinEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
