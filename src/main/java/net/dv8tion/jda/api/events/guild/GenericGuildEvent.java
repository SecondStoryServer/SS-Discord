
package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Guild Guild} event is fired.
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
     * The {@link net.dv8tion.jda.api.entities.Guild Guild}
     *
     * @return The Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return guild;
    }
}
