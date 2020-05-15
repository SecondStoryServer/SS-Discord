
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Guild Guild} event is fired.
 * <br>Every GuildEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any GuildEvent.
 */
public abstract class GenericGuildEvent extends Event
{
    protected final Guild guild;

    public GenericGuildEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild)
    {
        super(api, responseNumber);
        this.guild = guild;
    }

    /**
     * The {@link Guild Guild}
     *
     * @return The Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return guild;
    }
}
