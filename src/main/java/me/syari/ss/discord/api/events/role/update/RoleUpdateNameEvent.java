

package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link Role Role} updated its name.
 *
 * <p>Can be used to retrieve the old name.
 *
 * <p>Identifier: {@code name}
 */
public class RoleUpdateNameEvent extends GenericRoleUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public RoleUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, @Nonnull String oldName)
    {
        super(api, responseNumber, role, oldName, role.getName(), IDENTIFIER);
    }

    /**
     * The old name
     *
     * @return The old name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name
     *
     * @return The new name
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
