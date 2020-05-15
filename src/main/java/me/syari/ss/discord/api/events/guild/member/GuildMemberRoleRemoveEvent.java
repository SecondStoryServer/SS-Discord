
package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Indicates that one or more {@link Role Roles} were removed from a {@link Member Member}.
 * <br>This event requires {@link JDABuilder#setGuildSubscriptionsEnabled(boolean) guild subscriptions}
 * to be enabled.
 *
 * <p>Can be used to retrieve affected member and guild. Provides a list of removed roles.
 */
public class GuildMemberRoleRemoveEvent extends GenericGuildMemberEvent
{
    private final List<Role> removedRoles;

    public GuildMemberRoleRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull List<Role> removedRoles)
    {
        super(api, responseNumber, member);
        this.removedRoles = Collections.unmodifiableList(removedRoles);
    }

    /**
     * The removed roles
     *
     * @return The removed roles
     */
    public List<Role> getRoles()
    {
        return removedRoles;
    }
}
