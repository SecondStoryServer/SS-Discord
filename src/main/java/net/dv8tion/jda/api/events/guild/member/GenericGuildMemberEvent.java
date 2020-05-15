
package net.dv8tion.jda.api.events.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Guild Guild} member event is fired.
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
     * The {@link net.dv8tion.jda.api.entities.User User} instance
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
     * The {@link net.dv8tion.jda.api.entities.Member Member} instance
     *
     * @return The Member instance
     */
    @Nonnull
    public Member getMember()
    {
        return member;
    }
}
