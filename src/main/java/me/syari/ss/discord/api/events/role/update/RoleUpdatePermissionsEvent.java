

package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Indicates that a {@link Role Role} updated its permissions.
 *
 * <p>Can be used to retrieve the old permissions.
 *
 * <p>Identifier: {@code permission}
 */
public class RoleUpdatePermissionsEvent extends GenericRoleUpdateEvent<EnumSet<Permission>>
{
    public static final String IDENTIFIER = "permission";

    private final long oldPermissionsRaw;
    private final long newPermissionsRaw;

    public RoleUpdatePermissionsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, long oldPermissionsRaw)
    {
        super(api, responseNumber, role, Permission.getPermissions(oldPermissionsRaw), role.getPermissions(), IDENTIFIER);
        this.oldPermissionsRaw = oldPermissionsRaw;
        this.newPermissionsRaw = role.getPermissionsRaw();
    }

    /**
     * The old permissions
     *
     * @return The old permissions
     */
    @Nonnull
    public EnumSet<Permission> getOldPermissions()
    {
        return getOldValue();
    }

    /**
     * The old permissions
     *
     * @return The old permissions
     */
    public long getOldPermissionsRaw()
    {
        return oldPermissionsRaw;
    }

    /**
     * The new permissions
     *
     * @return The new permissions
     */
    @Nonnull
    public EnumSet<Permission> getNewPermissions()
    {
        return getNewValue();
    }

    /**
     * The new permissions
     *
     * @return The new permissions
     */
    public long getNewPermissionsRaw()
    {
        return newPermissionsRaw;
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getNewValue()
    {
        return super.getNewValue();
    }
}
