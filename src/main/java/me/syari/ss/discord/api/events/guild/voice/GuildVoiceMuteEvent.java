

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} was (un-)muted.
 * <br>Combines {@link GuildVoiceGuildMuteEvent GuildVoiceGuildMuteEvent}
 * and {@link GuildVoiceSelfMuteEvent GuildVoiceSelfMuteEvent}!
 *
 * <p>Can be used to detect when a member is muted or un-muted.
 */
public class GuildVoiceMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean muted;

    public GuildVoiceMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.muted = member.getVoiceState().isMuted();
    }

    /**
     * Whether the member was muted in this event.
     *
     * @return True, if the member was muted with this event
     *         <br>False, if the member was un-muted in this event
     */
    public boolean isMuted()
    {
        return muted;
    }
}
