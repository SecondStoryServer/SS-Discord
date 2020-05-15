

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild.MFALevel MFALevel} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when a MFALevel changes and retrieve the old one
 *
 * <p>Identifier: {@code mfa_level}
 */
public class GuildUpdateMFALevelEvent extends GenericGuildUpdateEvent<Guild.MFALevel>
{
    public static final String IDENTIFIER = "mfa_level";

    public GuildUpdateMFALevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.MFALevel oldMFALevel)
    {
        super(api, responseNumber, guild, oldMFALevel, guild.getRequiredMFALevel(), IDENTIFIER);
    }

    /**
     * The old {@link net.dv8tion.jda.api.entities.Guild.MFALevel MFALevel}
     *
     * @return The old MFALevel
     */
    @Nonnull
    public Guild.MFALevel getOldMFALevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link net.dv8tion.jda.api.entities.Guild.MFALevel MFALevel}
     *
     * @return The new MFALevel
     */
    @Nonnull
    public Guild.MFALevel getNewMFALevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.MFALevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.MFALevel getNewValue()
    {
        return super.getNewValue();
    }
}
