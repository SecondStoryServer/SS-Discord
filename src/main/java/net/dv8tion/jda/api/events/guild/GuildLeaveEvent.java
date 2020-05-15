
package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that you left a {@link net.dv8tion.jda.api.entities.Guild Guild}.
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
