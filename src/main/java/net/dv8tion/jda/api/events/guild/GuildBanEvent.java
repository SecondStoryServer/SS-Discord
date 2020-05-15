
package net.dv8tion.jda.api.events.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.User User} was banned from a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 *
 * <p>Can be used to retrieve the user who was banned (if available) and triggering guild.
 * <br><b>Note</b>: This does not directly indicate that a Member is removed from the Guild!
 *
 * @see net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent GuildMemberLeaveEvent
 */
public class GuildBanEvent extends GenericGuildEvent
{
    private final User user;

    public GuildBanEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull User user)
    {
        super(api, responseNumber, guild);
        this.user = user;
    }

    /**
     * The banned {@link net.dv8tion.jda.api.entities.User User}
     * <br>Possibly fake user.
     *
     * @return The banned user
     */
    @Nonnull
    public User getUser()
    {
        return user;
    }
}
