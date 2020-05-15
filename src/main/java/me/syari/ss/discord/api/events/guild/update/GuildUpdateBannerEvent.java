

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the {@link Guild#getBannerId() banner} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when the banner changes and retrieve the old one
 *
 * <p>Identifier: {@code banner}
 */
public class GuildUpdateBannerEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "banner";

    public GuildUpdateBannerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
    {
        super(api, responseNumber, guild, previous, guild.getBannerId(), IDENTIFIER);
    }

    /**
     * The new banner id
     *
     * @return The new banner id, or null if the banner was removed
     */
    @Nullable
    public String getNewBannerId()
    {
        return getNewValue();
    }

    /**
     * The new banner url
     *
     * @return The new banner url, or null if the banner was removed
     */
    @Nullable
    public String getNewBannerIdUrl()
    {
        return next == null ? null : String.format(Guild.BANNER_URL, guild.getId(), next);
    }

    /**
     * The old banner id
     *
     * @return The old banner id, or null if the banner didn't exist
     */
    @Nullable
    public String getOldBannerId()
    {
        return getOldValue();
    }

    /**
     * The old banner url
     *
     * @return The old banner url, or null if the banner didn't exist
     */
    @Nullable
    public String getOldBannerUrl()
    {
        return previous == null ? null : String.format(Guild.BANNER_URL, guild.getId(), previous);
    }
}
