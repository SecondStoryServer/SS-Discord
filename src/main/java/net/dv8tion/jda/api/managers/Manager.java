

package net.dv8tion.jda.api.managers;

import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.managers.ManagerBase;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public interface Manager<M extends Manager<M>> extends AuditableRestAction<Void>
{
    /**
     * Enables internal checks for missing permissions
     * <br>When this is disabled the chances of hitting a
     * {@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS ErrorResponse.MISSING_PERMISSIONS} is increased significantly,
     * otherwise JDA will check permissions and cancel the execution using
     * {@link net.dv8tion.jda.api.exceptions.InsufficientPermissionException InsufficientPermissionException}.
     * <br><b>Default: true</b>
     *
     * @param enable
     *        True, if JDA should perform permissions checks internally
     *
     * @see   #isPermissionChecksEnabled()
     */
    static void setPermissionChecksEnabled(boolean enable)
    {
        ManagerBase.setPermissionChecksEnabled(enable);
    }

    /**
     * Whether internal checks for missing permissions are enabled
     * <br>When this is disabled the chances of hitting a
     * {@link net.dv8tion.jda.api.requests.ErrorResponse#MISSING_PERMISSIONS ErrorResponse.MISSING_PERMISSIONS} is increased significantly,
     * otherwise JDA will check permissions and cancel the execution using
     * {@link net.dv8tion.jda.api.exceptions.InsufficientPermissionException InsufficientPermissionException}.
     *
     * @return True, if internal permission checks are enabled
     *
     * @see    #setPermissionChecksEnabled(boolean)
     */
    static boolean isPermissionChecksEnabled()
    {
        return ManagerBase.isPermissionChecksEnabled();
    }

    @Nonnull
    @Override
    M setCheck(BooleanSupplier checks);

    @Nonnull
    @CheckReturnValue
    M reset(long fields);

    @Nonnull
    @CheckReturnValue
    M reset(long... fields);

    /**
     * Resets all fields for this Manager
     *
     * @return The current Manager with all settings reset to default
     */
    @Nonnull
    @CheckReturnValue
    M reset();
}
