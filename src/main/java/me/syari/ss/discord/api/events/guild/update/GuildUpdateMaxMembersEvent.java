

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateMaxMembersEvent extends GenericGuildUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "max_members";

    public GuildUpdateMaxMembersEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
    {
        super(api, responseNumber, guild, previous, guild.getMaxMembers(), IDENTIFIER);
    }


    public int getOldMaxMembers()
    {
        return getOldValue();
    }


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
