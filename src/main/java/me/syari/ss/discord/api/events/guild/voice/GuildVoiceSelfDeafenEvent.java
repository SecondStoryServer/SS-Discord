

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} (un-)deafened itself.
 *
 * <p>Can be used to detect when a member deafens or un-deafens itself.
 */
public class GuildVoiceSelfDeafenEvent extends GenericGuildVoiceEvent
{
    protected final boolean selfDeafened;

    public GuildVoiceSelfDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.selfDeafened = member.getVoiceState().isSelfDeafened();
    }

    /**
     * Whether the member deafened itself in this event
     *
     * @return True, if the member deafened itself,
     *         <br>False, if the member un-deafened itself
     */
    public boolean isSelfDeafened()
    {
        return selfDeafened;
    }
}
