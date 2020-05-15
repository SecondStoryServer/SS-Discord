

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the name of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when a guild name changes and retrieve the old one
 *
 * <p>Identifier: {@code name}
 */
public class GuildUpdateNameEvent extends GenericGuildUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public GuildUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull String oldName)
    {
        super(api, responseNumber, guild, oldName, guild.getName(), IDENTIFIER);
    }

    /**
     * The old name
     *
     * @return The old name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name
     *
     * @return The new name
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
