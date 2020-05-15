

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} (un-)muted itself.
 *
 * <p>Can be used to detect when a member deafens or un-mutes itself.
 */
public class GuildVoiceSelfMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean selfMuted;

    public GuildVoiceSelfMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.selfMuted = member.getVoiceState().isSelfMuted();
    }

    /**
     * Whether the member muted itself in this event
     *
     * @return True, if the member muted itself,
     *         <br>False, if the member un-muted itself
     */
    public boolean isSelfMuted()
    {
        return selfMuted;
    }
}
