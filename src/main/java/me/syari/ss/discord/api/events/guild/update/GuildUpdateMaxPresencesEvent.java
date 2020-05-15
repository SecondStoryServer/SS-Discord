

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild#getMaxPresences() maximum presences limit} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when the maximum presences limit changes and retrieve the old one
 *
 * <p>Identifier: {@code max_presences}
 */
public class GuildUpdateMaxPresencesEvent extends GenericGuildUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "max_presences";

    public GuildUpdateMaxPresencesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
    {
        super(api, responseNumber, guild, previous, guild.getMaxPresences(), IDENTIFIER);
    }

    /**
     * The old max presences
     *
     * @return The old max presences
     */
    public int getOldMaxPresences()
    {
        return getOldValue();
    }

    /**
     * The new max presences
     *
     * @return The new max presences
     */
    public int getNewMaxPresences()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Integer getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Integer getNewValue()
    {
        return super.getNewValue();
    }
}
