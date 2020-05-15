
package net.dv8tion.jda.api.events.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} joined a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
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
