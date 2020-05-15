

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} was (un-)deafened by a moderator.
 *
 * <p>Can be used to detect when a member is deafened or un-deafened by a moderator.
 */
public class GuildVoiceGuildDeafenEvent extends GenericGuildVoiceEvent
{
    protected final boolean guildDeafened;

    public GuildVoiceGuildDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.guildDeafened = member.getVoiceState().isGuildDeafened();
    }

    /**
     * Whether the member was deafened by a moderator in this event
     *
     * @return True, if a moderator deafened this member,
     *         <br>False, if a moderator un-deafened this member
     */
    public boolean isGuildDeafened()
    {
        return guildDeafened;
    }
}
