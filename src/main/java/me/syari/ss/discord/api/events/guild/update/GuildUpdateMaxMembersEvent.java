

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;

/**
 * Indicates that the {@link Guild#getMaxMembers() maximum member limit} of a {@link Guild Guild} changed.
 *
 * <p>Can be used to detect when the maximum member limit changes and retrieve the old one
 *
 * <p>Identifier: {@code max_members}
 */
public class GuildUpdateMaxMembersEvent extends GenericGuildUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "max_members";

    public GuildUpdateMaxMembersEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
    {
        super(api, responseNumber, guild, previous, guild.getMaxMembers(), IDENTIFIER);
    }

    /**
     * The old max members for this guild
     *
     * @return The old max members for this guild
     */
    public int getOldMaxMembers()
    {
        return getOldValue();
    }

    /**
     * The new max members for this guild
     *
     * @return The new max members for this guild
     */
    public int getNewMaxMembers()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public Integer getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Integer getNewValue()
    {
        return super.getNewValue();
    }
}
