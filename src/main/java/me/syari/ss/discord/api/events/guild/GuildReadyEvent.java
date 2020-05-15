

package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.events.ReconnectedEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Guild Guild} finished setting up
 * <br>This event is fired if a guild finished setting up during login phase.
 * After this event is fired, JDA will start dispatching events related to this guild.
 * This indicates a guild was created and added to the cache. It will be fired for both the initial
 * setup and full reconnects (indicated by {@link ReconnectedEvent ReconnectedEvent}).
 *
 * <p>Can be used to initialize any services that depend on this guild.
 */
public class GuildReadyEvent extends GenericGuildEvent
{
    public GuildReadyEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber, guild);
    }
}
