
package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Guild Guild} member event is fired.
 * <br>Every GuildMemberEvent is an instance of this event and can be casted.
 *
 * <p>Can be used to detect any GuildMemberEvent.
 */
public abstract class GenericGuildMemberEvent extends GenericGuildEvent
{
    private final Member member;

    public GenericGuildMemberEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member)
    {
        super(api, responseNumber, member.getGuild());
        this.member = member;
    }

    /**
     * The {@link User User} instance
     * <br>Shortcut for {@code getMember().getUser()}
     *
     * @return The User instance
     */
    @Nonnull
    public User getUser()
    {
        return getMember().getUser();
    }

    /**
     * The {@link Member Member} instance
     *
     * @return The Member instance
     */
    @Nonnull
    public Member getMember()
    {
        return member;
    }
}
