

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild.VerificationLevel VerificationLevel} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
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
     * The old {@link net.dv8tion.jda.api.entities.Guild.VerificationLevel VerificationLevel}
     *
     * @return The old VerificationLevel
     */
    @Nonnull
    public Guild.VerificationLevel getOldVerificationLevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link net.dv8tion.jda.api.entities.Guild.VerificationLevel VerificationLevel}
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
