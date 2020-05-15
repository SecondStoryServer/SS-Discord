

package me.syari.ss.discord.api.events.guild.member.update;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * Indicates that a {@link Member Member} updated their {@link Guild Guild} boost time.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 * <br>This happens when a member started or stopped boosting a guild.
 *
 * <p>Can be used to retrieve members who boosted, triggering guild.
 *
 * <p>Identifier: {@code boost_time}
 */
public class GuildMemberUpdateBoostTimeEvent extends GenericGuildMemberUpdateEvent<OffsetDateTime>
{
    public static final String IDENTIFIER = "boost_time";

    public GuildMemberUpdateBoostTimeEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nullable OffsetDateTime previous)
    {
        super(api, responseNumber, member, previous, member.getTimeBoosted(), IDENTIFIER);
    }

    /**
     * The old boost time
     *
     * @return The old boost time
     */
    @Nullable
    public OffsetDateTime getOldTimeBoosted()
    {
        return getOldValue();
    }

    /**
     * The new boost time
     *
     * @return The new boost time
     */
    @Nullable
    public OffsetDateTime getNewTimeBoosted()
    {
        return getNewValue();
    }
}
