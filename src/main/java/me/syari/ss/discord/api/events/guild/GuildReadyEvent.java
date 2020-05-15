

package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.events.ReconnectedEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildReadyEvent extends GenericGuildEvent
{
    public GuildReadyEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
