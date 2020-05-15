

package me.syari.ss.discord.api.events.guild.member.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;


public class GuildMemberUpdateBoostTimeEvent extends GenericGuildMemberUpdateEvent<OffsetDateTime>
{
    public static final String IDENTIFIER = "boost_time";

    public GuildMemberUpdateBoostTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable OffsetDateTime previous)
    {
        super(api, responseNumber, member, previous, member.getTimeBoosted(), IDENTIFIER);
    }


    @Nullable
    public OffsetDateTime getOldTimeBoosted()
    {
        return getOldValue();
    }


    @Nullable
    public OffsetDateTime getNewTimeBoosted()
    {
        return getNewValue();
    }
}
