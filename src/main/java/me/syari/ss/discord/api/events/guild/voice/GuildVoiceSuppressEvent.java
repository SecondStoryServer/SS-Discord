

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} was (un-)suppressed.
 *
 * <p>Can be used to detect when a member is suppressed or un-suppressed.
 *
 * @see GuildVoiceState#isSuppressed() GuildVoiceState.isSuppressed()
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
