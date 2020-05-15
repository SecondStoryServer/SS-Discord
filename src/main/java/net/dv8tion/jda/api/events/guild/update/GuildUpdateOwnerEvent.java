

package net.dv8tion.jda.api.events.guild.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that the owner of a {@link net.dv8tion.jda.api.entities.Guild Guild} changed.
 *
 * <p>Can be used to detect when an owner of a guild changes and retrieve the old one
 *
 * <p>Identifier: {@code owner}
 */
public class GuildUpdateOwnerEvent extends GenericGuildUpdateEvent<Member>
{
    public static final String IDENTIFIER = "owner";
    private final long prevId, nextId;

    public GuildUpdateOwnerEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable Member oldOwner,
                                 long prevId, long nextId)
    {
        super(api, responseNumber, guild, oldOwner, guild.getOwner(), IDENTIFIER);
        this.prevId = prevId;
        this.nextId = nextId;
    }

    /**
     * The previous owner user id
     *
     * @return The previous owner id
     */
    public long getNewOwnerIdLong()
    {
        return nextId;
    }

    /**
     * The previous owner user id
     *
     * @return The previous owner id
     */
    @Nonnull
    public String getNewOwnerId()
    {
        return Long.toUnsignedString(nextId);
    }

    /**
     * The new owner user id
     *
     * @return The new owner id
     */
    public long getOldOwnerIdLong()
    {
        return prevId;
    }

    /**
     * The new owner user id
     *
     * @return The new owner id
     */
    @Nonnull
    public String getOldOwnerId()
    {
        return Long.toUnsignedString(prevId);
    }

    /**
     * The old owner
     *
     * @return The old owner
     */
    @Nullable
    public Member getOldOwner()
    {
        return getOldValue();
    }

    /**
     * The new owner
     *
     * @return The new owner
     */
    @Nullable
    public Member getNewOwner()
    {
        return getNewValue();
    }
}
