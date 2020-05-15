

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel ExplicitContentLevel} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when an ExplicitContentLevel changes and retrieve the old one
 *
 * <p>Identifier: {@code explicit_content_filter}
 */
public class GuildUpdateExplicitContentLevelEvent extends GenericGuildUpdateEvent<Guild.ExplicitContentLevel>
{
    public static final String IDENTIFIER = "explicit_content_filter";

    public GuildUpdateExplicitContentLevelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.ExplicitContentLevel oldLevel)
    {
        super(api, responseNumber, guild, oldLevel, guild.getExplicitContentLevel(), IDENTIFIER);
    }

    /**
     * The old {@link net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel ExplicitContentLevel} for the
     * {@link net.dv8tion.jda.api.entities.Guild Guild} prior to this event.
     *
     * @return The old explicit content level
     */
    @Nonnull
    public Guild.ExplicitContentLevel getOldLevel()
    {
        return getOldValue();
    }

    /**
     * The new {@link net.dv8tion.jda.api.entities.Guild.ExplicitContentLevel ExplicitContentLevel} for the
     * {@link net.dv8tion.jda.api.entities.Guild Guild} after to this event.
     *
     * @return The new explicit content level
     */
    @Nonnull
    public Guild.ExplicitContentLevel getNewLevel()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.ExplicitContentLevel getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.ExplicitContentLevel getNewValue()
    {
        return super.getNewValue();
    }
}
