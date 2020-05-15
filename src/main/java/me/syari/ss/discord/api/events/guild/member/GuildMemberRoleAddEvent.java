
package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that one or more {@link Role Roles} were assigned to a {@link Member Member}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve affected member and guild. Provides a list of added roles.
 */
public class GuildMemberRoleAddEvent extends GenericGuildMemberEvent
{
    private final List<Role> addedRoles;

    public GuildMemberRoleAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull List<Role> addedRoles)
    {
        super(api, responseNumber, member);
        this.addedRoles = Collections.unmodifiableList(addedRoles);
    }

    /**
     * The list of roles that were added
     *
     * @return The list of roles that were added
     */
    @Nonnull
    public List<Role> getRoles()
    {
        return addedRoles;
    }
}
