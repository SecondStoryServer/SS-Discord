

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild.Timeout AFK-Timeout} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when an afk timeout changes and retrieve the old one
 *
 * <p>Identifier: {@code afk_timeout}
 */
public class GuildUpdateAfkTimeoutEvent extends GenericGuildUpdateEvent<Guild.Timeout>
{
    public static final String IDENTIFIER = "afk_timeout";

    public GuildUpdateAfkTimeoutEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Guild.Timeout oldAfkTimeout)
    {
        super(api, responseNumber, guild, oldAfkTimeout, guild.getAfkTimeout(), IDENTIFIER);
    }

    /**
     * The old {@link net.dv8tion.jda.api.entities.Guild.Timeout AFK-Timeout}
     *
     * @return The old AFK-Timeout
     */
    @Nonnull
    public Guild.Timeout getOldAfkTimeout()
    {
        return getOldValue();
    }

    /**
     * The new {@link net.dv8tion.jda.api.entities.Guild.Timeout AFK-Timeout}
     *
     * @return The new AFK-Timeout
     */
    @Nonnull
    public Guild.Timeout getNewAfkTimeout()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Guild.Timeout getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Guild.Timeout getNewValue()
    {
        return super.getNewValue();
    }
}
