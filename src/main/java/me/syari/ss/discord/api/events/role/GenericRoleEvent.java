package me.syari.ss.discord.api.events.role;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericRoleEvent extends Event {
    protected final Role role;

    public GenericRoleEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role) {
        super(api, responseNumber);
        this.role = role;
    }


    @Nonnull
    public Role getRole() {
        return role;
    }


    @Nonnull
    public Guild getGuild() {
        return role.getGuild();
    }
}
