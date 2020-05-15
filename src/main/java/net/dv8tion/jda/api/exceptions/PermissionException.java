
package net.dv8tion.jda.api.exceptions;

import net.dv8tion.jda.api.Permission;

/**
 * Indicates that the currently logged in account does not meet the specified {@link net.dv8tion.jda.api.Permission Permission}
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
     *        The required {@link net.dv8tion.jda.api.Permission Permission}
     */
    protected PermissionException(Permission permission)
    {
        this(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
    }

    /**
     * Creates a new PermissionException
     *
     * @param permission
     *        The required {@link net.dv8tion.jda.api.Permission Permission}
     * @param reason
     *        The reason for this Exception
     */
    protected PermissionException(Permission permission, String reason)
    {
        super(reason);
        this.permission = permission;
    }

    /**
     * The {@link net.dv8tion.jda.api.Permission Permission} that is required for the operation
     *
     * <p><b>If this is a {@link net.dv8tion.jda.api.exceptions.HierarchyException HierarchyException}
     * this will always be {@link net.dv8tion.jda.api.Permission#UNKNOWN Permission.UNKNOWN}!</b>
     *
     * @return The required {@link net.dv8tion.jda.api.Permission Permission}
     */
    public Permission getPermission()
    {
        return permission;
    }
}
