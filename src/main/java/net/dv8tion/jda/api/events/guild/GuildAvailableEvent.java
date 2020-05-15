

package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Guild Guild} became available.
 * <br>This is fired when a guild that was previously marked as unavailable by a {@link GuildUnavailableEvent} has become available again.
 *
 * <p>Can be used to detect that a Guild will now start sending events and can be interacted with.
 */
public class GuildAvailableEvent extends GenericGuildEvent
{
    public GuildAvailableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
