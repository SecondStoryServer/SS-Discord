

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild#getBoostTier() boost tier} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when the boost tier changes and retrieve the old one
 *
 * <p>Identifier: {@code boost_tier}
 */
public class GuildUpdateBoostTierEvent extends GenericGuildUpdateEvent<Guild.BoostTier>
{
    public static final String IDENTIFIER = "boost_tier";

    public GuildUpdateBoostTierEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.BoostTier previous)
    {
        super(api, responseNumber, guild, previous, guild.getBoostTier(), IDENTIFIER);
    }

    /**
     * The old {@link Guild.BoostTier}
     *
     * @return The old BoostTier
     */
    @Nonnull
    public Guild.BoostTier getOldBoostTier()
    {
        return getOldValue();
    }

    /**
     * The new {@link Guild.BoostTier}
     *
     * @return The new BoostTier
     */
    @Nonnull
    public Guild.BoostTier getNewBoostTier()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.BoostTier getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.BoostTier getNewValue()
    {
        return super.getNewValue();
    }
}
