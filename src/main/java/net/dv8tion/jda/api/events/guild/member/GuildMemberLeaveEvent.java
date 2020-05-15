
package net.dv8tion.jda.api.events.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates a {@link net.dv8tion.jda.api.entities.Member Member} left a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve members who leave a guild.
 */
public class GuildMemberLeaveEvent extends GenericGuildMemberEvent
{
    public GuildMemberLeaveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
    }
}
