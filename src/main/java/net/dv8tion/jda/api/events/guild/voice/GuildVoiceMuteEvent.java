

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} was (un-)muted.
 * <br>Combines {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent GuildVoiceGuildMuteEvent}
 * and {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent GuildVoiceSelfMuteEvent}!
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
