
package me.syari.ss.discord.api.events.guild;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link User User} was unbanned from a {@link Guild Guild}.
 *
 * <p>Can be used to retrieve the user who was unbanned (if available) and the guild which they were unbanned from.
 */
public class GuildUnbanEvent extends GenericGuildEvent
{
    private final User user;

    public GuildUnbanEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull User user)
    {
        super(api, responseNumber, guild);
        this.user = user;
    }

    /**
     * The {@link User User} who was unbanned
     * <br>Possibly fake user.
     *
     * @return The unbanned user
     */
    @Nonnull
    public User getUser()
    {
        return user;
    }
}
