package me.syari.ss.discord.api.events.role;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleCreateEvent extends GenericRoleEvent {
    public RoleCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role createdRole) {
        super(api, responseNumber, createdRole);
    }
}
