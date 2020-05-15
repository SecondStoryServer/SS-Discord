
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that you joined a {@link Guild Guild}.
 * <br>This requires that the guild is available when the guild leave happens. Otherwise a {@link UnavailableGuildJoinedEvent} is fired instead.
 *
 * <p><b>Warning: Discord already triggered a mass amount of these events due to a downtime. Be careful!</b>
 *
 * @see UnavailableGuildJoinedEvent
 */
public class GuildJoinEvent extends GenericGuildEvent
{
    public GuildJoinEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
