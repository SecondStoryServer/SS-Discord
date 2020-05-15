

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} (un-)muted itself.
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
