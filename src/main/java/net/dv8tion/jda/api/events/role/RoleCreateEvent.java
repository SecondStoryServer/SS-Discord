

package net.dv8tion.jda.api.events.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} was created.
 *
 * <p>Can be used to retrieve the created Role and its Guild.
 */
public class RoleCreateEvent extends GenericRoleEvent
{
    public RoleCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role createdRole)
    {
        super(api, responseNumber, createdRole);
    }
}
