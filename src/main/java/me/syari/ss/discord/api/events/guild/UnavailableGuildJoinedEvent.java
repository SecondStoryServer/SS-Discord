

package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that you joined a {@link Guild Guild} that is not yet available.
 * <b>This does not extend {@link GenericGuildEvent GenericGuildEvent}</b>
 *
 * <p>Can be used to retrieve id of new unavailable Guild.
 */
public class UnavailableGuildJoinedEvent extends Event
{
    private final long guildId;

    public UnavailableGuildJoinedEvent(@Nonnull JDA api, long responseNumber, long guildId)
    {
        super(api, responseNumber);
        this.guildId = guildId;
    }

    /**
     * The ID of the guild
     *
     * @return The ID of the guild
     */
    @Nonnull
    public String getGuildId()
    {
        return Long.toUnsignedString(guildId);
    }

    /**
     * The ID of the guild
     *
     * @return The ID of the guild
     */
    public long getGuildIdLong()
    {
        return guildId;
    }
}
