

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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


    public long getNewOwnerIdLong()
    {
        return nextId;
    }


    @Nonnull
    public String getNewOwnerId()
    {
        return Long.toUnsignedString(nextId);
    }


    public long getOldOwnerIdLong()
    {
        return prevId;
    }


    @Nonnull
    public String getOldOwnerId()
    {
        return Long.toUnsignedString(prevId);
    }


    @Nullable
    public Member getOldOwner()
    {
        return getOldValue();
    }


    @Nullable
    public Member getNewOwner()
    {
        return getNewValue();
    }
}
