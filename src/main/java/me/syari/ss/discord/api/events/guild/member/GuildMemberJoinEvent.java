
package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} joined a {@link Guild Guild}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve members who join a guild.
 */
public class GuildMemberJoinEvent extends GenericGuildMemberEvent
{
    public GuildMemberJoinEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
    }
}
