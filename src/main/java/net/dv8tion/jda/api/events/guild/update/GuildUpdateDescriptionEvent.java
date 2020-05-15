

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the {@link net.dv8tion.jda.api.entities.Guild#getDescription() description} of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when the description changes and retrieve the old one
 *
 * <p>Identifier: {@code description}
 */
public class GuildUpdateDescriptionEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "description";

    public GuildUpdateDescriptionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable String previous)
    {
        super(api, responseNumber, guild, previous, guild.getDescription(), IDENTIFIER);
    }

    /**
     * The old description for this guild
     *
     * @return The old description for this guild, or null if none was set
     */
    @Nullable
    public String getOldDescription()
    {
        return getOldValue();
    }

    /**
     * The new description for this guild
     *
     * @return The new description, or null if it was removed
     */
    @Nullable
    public String getNewDescription()
    {
        return getNewValue();
    }
}
