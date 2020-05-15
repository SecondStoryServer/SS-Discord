

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the splash of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a guild splash changes and retrieve the old one
 *
 * <p>Identifier: {@code splash}
 */
public class GuildUpdateSplashEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "splash";

    public GuildUpdateSplashEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldSplashId)
    {
        super(api, responseNumber, guild, oldSplashId, guild.getSplashId(), IDENTIFIER);
    }

    /**
     * The old splash id
     *
     * @return The old splash id, or null
     */
    @Nullable
    public String getOldSplashId()
    {
        return getOldValue();
    }

    /**
     * The url of the old splash
     *
     * @return The url of the old splash, or null
     */
    @Nullable
    public String getOldSplashUrl()
    {
        return previous == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), previous);
    }

    /**
     * The new splash id
     *
     * @return The new splash id, or null
     */
    @Nullable
    public String getNewSplashId()
    {
        return getNewValue();
    }

    /**
     * The url of the new splash
     *
     * @return The url of the new splash, or null
     */
    @Nullable
    public String getNewSplashUrl()
    {
        return next == null ? null : String.format(Guild.SPLASH_URL, guild.getId(), next);
    }
}
