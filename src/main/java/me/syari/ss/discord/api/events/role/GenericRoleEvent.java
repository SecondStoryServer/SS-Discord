

package me.syari.ss.discord.api.events.role;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Role Role} was created/deleted/changed.
 * <br>Every RoleEvent is derived from this event and can be casted.
 *
 * <p>Can be used to detect any RoleEvent.
 */
public abstract class GenericRoleEvent extends Event
{
    protected final Role role;

    public GenericRoleEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role)
    {
        super(api, responseNumber);
        this.role = role;
    }

    /**
     * The role for this event
     *
     * @return The role for this event
     */
    @Nonnull
    public Role getRole()
    {
        return role;
    }

    /**
     * The guild of the role
     *
     * @return The guild of the role
     */
    @Nonnull
    public Guild getGuild()
    {
        return role.getGuild();
    }
}
