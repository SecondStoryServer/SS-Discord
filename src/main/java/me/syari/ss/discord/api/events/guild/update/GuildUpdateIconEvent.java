

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the Icon of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when a guild icon changes and retrieve the old one
 *
 * <p>Identifier: {@code icon}
 */
public class GuildUpdateIconEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "icon";

    public GuildUpdateIconEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String oldIconId)
    {
        super(api, responseNumber, guild, oldIconId, guild.getIconId(), IDENTIFIER);
    }

    /**
     * The old icon id
     *
     * @return The old icon id, or null
     */
    @Nullable
    public String getOldIconId()
    {
        return getOldValue();
    }

    /**
     * The url of the old icon
     *
     * @return The url of the old icon, or null
     */
    @Nullable
    public String getOldIconUrl()
    {
        return previous == null ? null : String.format(Guild.ICON_URL, guild.getId(), previous, previous.startsWith("a_") ? "gif" : "png");
    }

    /**
     * The old icon id
     *
     * @return The old icon id, or null
     */
    @Nullable
    public String getNewIconId()
    {
        return getNewValue();
    }

    /**
     * The url of the new icon
     *
     * @return The url of the new icon, or null
     */
    @Nullable
    public String getNewIconUrl()
    {
        return next == null ? null : String.format(Guild.ICON_URL, guild.getId(), next, next.startsWith("a_") ? "gif" : "png");
    }
}
