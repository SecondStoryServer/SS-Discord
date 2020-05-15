

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild#getVanityUrl() vanity url} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when the vanity url changes and retrieve the old one
 *
 * <p>Identifier: {@code vanity_code}
 */
public class GuildUpdateVanityCodeEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "vanity_code";

    public GuildUpdateVanityCodeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
    {
        super(api, responseNumber, guild, previous, guild.getVanityCode(), IDENTIFIER);
    }

    /**
     * The old vanity code
     *
     * @return The old vanity code
     */
    @Nullable
    public String getOldVanityCode()
    {
        return getOldValue();
    }

    /**
     * The old vanity url
     *
     * @return The old vanity url
     */
    @Nullable
    public String getOldVanityUrl()
    {
        return getOldVanityCode() == null ? null : "https://discord.gg/" + getOldVanityCode();
    }

    /**
     * The new vanity code
     *
     * @return The new vanity code
     */
    @Nullable
    public String getNewVanityCode()
    {
        return getNewValue();
    }

    /**
     * The new vanity url
     *
     * @return The new vanity url
     */
    @Nullable
    public String getNewVanityUrl()
    {
        return getNewVanityCode() == null ? null : "https://discord.gg/" + getNewVanityCode();
    }
}
