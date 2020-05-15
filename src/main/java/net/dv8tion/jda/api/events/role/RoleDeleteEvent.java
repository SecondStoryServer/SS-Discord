

package net.dv8tion.jda.api.events.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} was deleted.
 *
 * <p>Can be used to retrieve the deleted Role and its Guild.
 */
public class RoleDeleteEvent extends GenericRoleEvent
{
    public RoleDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role deletedRole)
    {
        super(api, responseNumber, deletedRole);
    }
}
