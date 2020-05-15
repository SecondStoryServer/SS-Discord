

package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that you left a {@link net.dv8tion.jda.api.entities.Guild Guild} that is not yet available.
 * <b>This does not extend {@link net.dv8tion.jda.api.events.guild.GenericGuildEvent GenericGuildEvent}</b>
 *
 * <p>Can be used to retrieve id of the unavailable Guild.
 */
public class UnavailableGuildLeaveEvent extends Event
{
    private final long guildId;

    public UnavailableGuildLeaveEvent(@Nonnull JDA api, long responseNumber, long guildId)
    {
        super(api, responseNumber);
        this.guildId = guildId;
    }

    /**
     * The id for the guild we left.
     *
     * @return The id for the guild
     */
    @Nonnull
    public String getGuildId()
    {
        return Long.toUnsignedString(guildId);
    }

    /**
     * The id for the guild we left.
     *
     * @return The id for the guild
     */
    public long getGuildIdLong()
    {
        return guildId;
    }
}
