

package me.syari.ss.discord.api.events.role;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleDeleteEvent extends GenericRoleEvent
{
    public RoleDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role deletedRole)
    {
        super(api, responseNumber, deletedRole);
    }
}
