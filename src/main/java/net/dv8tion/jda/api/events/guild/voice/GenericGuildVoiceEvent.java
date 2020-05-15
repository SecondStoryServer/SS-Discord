

package net.dv8tion.jda.api.events.guild.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Guild Guild} voice event is fired.
 * <br>Every GuildVoiceEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any GuildVoiceEvent.
 */
public abstract class GenericGuildVoiceEvent extends GenericGuildEvent
{
    protected final Member member;

    public GenericGuildVoiceEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member.getGuild());
        this.member = member;
    }

    /**
     * The affected {@link net.dv8tion.jda.api.entities.Member Member}
     *
     * @return The affected Member
     */
    @Nonnull
    public Member getMember()
    {
        return member;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.GuildVoiceState GuildVoiceState} of the member
     * <br>Shortcut for {@code getMember().getVoiceState()}
     *
     * @return The {@link net.dv8tion.jda.api.entities.GuildVoiceState GuildVoiceState} of the member
     */
    @Nonnull
    public GuildVoiceState getVoiceState()
    {
        return member.getVoiceState();
    }
}
