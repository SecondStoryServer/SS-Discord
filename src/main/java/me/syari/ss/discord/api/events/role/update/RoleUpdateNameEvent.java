package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleUpdateNameEvent extends GenericRoleUpdateEvent<String> {
    public static final String IDENTIFIER = "name";

    public RoleUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, @Nonnull String oldName) {
        super(api, responseNumber, role, oldName, role.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName() {
        return getOldValue();
    }


    @Nonnull
    public String getNewName() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue() {
        return super.getNewValue();
    }
}
