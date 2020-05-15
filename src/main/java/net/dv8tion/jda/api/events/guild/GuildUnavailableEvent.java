

package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Guild Guild} became unavailable.
 * <br>Possibly due to a downtime or an outage. When it becomes available again a {@link GuildAvailableEvent} will be fired.
 *
 * <p>Can be used to detect that a Guild stopped responding.
 */
public class GuildUnavailableEvent extends GenericGuildEvent
{
    public GuildUnavailableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
