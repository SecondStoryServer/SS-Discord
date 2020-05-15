

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Member Member} was (un-)deafened.
 * <br>Combines {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent GuildVoiceGuildDeafenEvent}
 * and {@link net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent GuildVoiceSelfDeafenEvent}!
 *
 * <p>Can be used to detect when a member is deafened or un-deafened.
 */
public class GuildVoiceDeafenEvent extends GenericGuildVoiceEvent
{
    protected final boolean deafened;

    public GuildVoiceDeafenEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member);
        this.deafened = member.getVoiceState().isDeafened();
    }

    /**
     * Whether the member was deafened in this event.
     *
     * @return True, if the member was deafened with this event
     *         <br>False, if the member was un-deafened in this event
     */
    public boolean isDeafened()
    {
        return deafened;
    }
}
