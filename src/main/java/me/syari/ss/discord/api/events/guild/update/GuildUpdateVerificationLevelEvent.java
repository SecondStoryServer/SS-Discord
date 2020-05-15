

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild.VerificationLevel VerificationLevel} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a VerificationLevel changes and retrieve the old one
 *
 * <p>Identifier: {@code verification_level}
 */
public class GuildUpdateVerificationLevelEvent extends GenericGuildUpdateEvent<Guild.VerificationLevel>
{
    public static final String IDENTIFIER = "verification_level";

    public GuildUpdateVerificationLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.VerificationLevel oldVerificationLevel)
    {
        super(api, responseNumber, guild, oldVerificationLevel, guild.getVerificationLevel(), IDENTIFIER);
    }

    /**
     * The old {@link Guild.VerificationLevel VerificationLevel}
     *
     * @return The old VerificationLevel
     */
    @Nonnull
    public Guild.VerificationLevel getOldVerificationLevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link Guild.VerificationLevel VerificationLevel}
     *
     * @return The new VerificationLevel
     */
    @Nonnull
    public Guild.VerificationLevel getNewVerificationLevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.VerificationLevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.VerificationLevel getNewValue()
    {
        return super.getNewValue();
    }
}
