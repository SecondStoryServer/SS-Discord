

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild#getBoostCount() boost count} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when the boost count changes and retrieve the old one
 *
 * <p>Identifier: {@code boost_count}
 */
public class GuildUpdateBoostCountEvent extends GenericGuildUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "boost_count";

    public GuildUpdateBoostCountEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
    {
        super(api, responseNumber, guild, previous, guild.getBoostCount(), IDENTIFIER);
    }

    /**
     * The old boost count
     *
     * @return The old boost count
     */
    public int getOldBoostCount()
    {
        return getOldValue();
    }

    /**
     * The new boost count
     *
     * @return The new boost count
     */
    public int getNewBoostCount()
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
