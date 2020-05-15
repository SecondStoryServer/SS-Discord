
package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.Permission;

/**
 * Indicates that the currently logged in account does not meet the specified {@link Permission Permission}
 * from {@link #getPermission()}
 */
public class PermissionException extends RuntimeException
{
    private final Permission permission;

    /**
     * Creates a new PermissionException instance
     *
     * @param reason
     *        The reason for this Exception
     */
    public PermissionException(String reason)
    {
        this(Permission.UNKNOWN, reason);
    }

    /**
     * Creates a new PermissionException instance
     *
     * @param permission
     *        The required {@link Permission Permission}
     */
    protected PermissionException(Permission permission)
    {
        this(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
    }

    /**
     * Creates a new PermissionException
     *
     * @param permission
     *        The required {@link Permission Permission}
     * @param reason
     *        The reason for this Exception
     */
    protected PermissionException(Permission permission, String reason)
    {
        super(reason);
        this.permission = permission;
    }

    /**
     * The {@link Permission Permission} that is required for the operation
     *
     * <p><b>If this is a {@link HierarchyException HierarchyException}
     * this will always be {@link Permission#UNKNOWN Permission.UNKNOWN}!</b>
     *
     * @return The required {@link Permission Permission}
     */
    public Permission getPermission()
    {
        return permission;
    }
}
