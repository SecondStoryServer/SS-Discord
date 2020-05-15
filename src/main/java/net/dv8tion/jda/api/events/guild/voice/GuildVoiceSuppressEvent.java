

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} was (un-)suppressed.
 *
 * <p>Can be used to detect when a member is suppressed or un-suppressed.
 *
 * @see net.dv8tion.jda.api.entities.GuildVoiceState#isSuppressed() GuildVoiceState.isSuppressed()
 */
public class GuildVoiceSuppressEvent extends GenericGuildVoiceEvent
{
    protected final boolean suppressed;

    public GuildVoiceSuppressEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.suppressed = member.getVoiceState().isSuppressed();
    }

    /**
     * Whether the member was suppressed in this event
     *
     * @return True, if the member was suppressed,
     *         <br>False, if the member was un-suppressed
     */
    public boolean isSuppressed()
    {
        return suppressed;
    }
}
