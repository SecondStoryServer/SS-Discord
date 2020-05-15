
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that you left a {@link Guild Guild}.
 * <br>This requires that the guild is available when the guild leave happens. Otherwise a {@link UnavailableGuildLeaveEvent} is fired instead.
 *
 * <p>Can be used to detect when you leave a Guild.
 *
 * @see UnavailableGuildLeaveEvent
 */
public class GuildLeaveEvent extends GenericGuildEvent
{
    public GuildLeaveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
