
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.events.guild.member.GuildMemberLeaveEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link User User} was banned from a {@link Guild Guild}.
 *
 * <p>Can be used to retrieve the user who was banned (if available) and triggering guild.
 * <br><b>Note</b>: This does not directly indicate that a Member is removed from the Guild!
 *
 * @see GuildMemberLeaveEvent GuildMemberLeaveEvent
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
     * The banned {@link User User}
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
