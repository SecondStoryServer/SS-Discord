

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Member Member} was (un-)muted by a moderator.
 *
 * <p>Can be used to detect when a member is muted or un-muted by a moderator.
 */
public class GuildVoiceGuildMuteEvent extends GenericGuildVoiceEvent
{
    protected final boolean guildMuted;

    public GuildVoiceGuildMuteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.guildMuted = member.getVoiceState().isGuildMuted();
    }

    /**
     * Whether the member was muted by a moderator in this event
     *
     * @return True, if a moderator muted this member,
     *         <br>False, if a moderator un-muted this member
     */
    public boolean isGuildMuted()
    {
        return guildMuted;
    }
}
