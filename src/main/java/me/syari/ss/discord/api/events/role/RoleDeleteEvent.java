

package me.syari.ss.discord.api.events.role;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Role Role} was deleted.
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
