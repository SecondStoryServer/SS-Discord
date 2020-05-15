

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} (un-)deafened itself.
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
