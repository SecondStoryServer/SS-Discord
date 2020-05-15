

package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.entities.Member;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Guild Guild} voice event is fired.
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
     * The affected {@link Member Member}
     *
     * @return The affected Member
     */
    @Nonnull
    public Member getMember()
    {
        return member;
    }

    /**
     * The {@link GuildVoiceState GuildVoiceState} of the member
     * <br>Shortcut for {@code getMember().getVoiceState()}
     *
     * @return The {@link GuildVoiceState GuildVoiceState} of the member
     */
    @Nonnull
    public GuildVoiceState getVoiceState()
    {
        return member.getVoiceState();
    }
}
