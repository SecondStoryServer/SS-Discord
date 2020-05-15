
package net.dv8tion.jda.api.events.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that one or more {@link net.dv8tion.jda.api.entities.Role Roles} were assigned to a {@link net.dv8tion.jda.api.entities.Member Member}.
 * <br>This event requires {@link net.dv8tion.jda.api.JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
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
