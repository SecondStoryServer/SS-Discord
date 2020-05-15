

package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;


public class GuildUpdateBoostCountEvent extends GenericGuildUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "boost_count";

    public GuildUpdateBoostCountEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, int previous)
    {
        super(api, responseNumber, guild, previous, guild.getBoostCount(), IDENTIFIER);
    }


    public int getOldBoostCount()
    {
        return getOldValue();
    }


    public int getNewBoostCount()
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
