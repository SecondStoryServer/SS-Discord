

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.managers.ManagerBase;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public interface Manager<M extends Manager<M>> extends AuditableRestAction<Void>
{
    /**
     * Enables internal checks for missing permissions
     * <br>When this is disabled the chances of hitting a
     * {@link ErrorResponse#MISSING_PERMISSIONS ErrorResponse.MISSING_PERMISSIONS} is increased significantly,
     * otherwise JDA will check permissions and cancel the execution using
     * {@link InsufficientPermissionException InsufficientPermissionException}.
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
     * {@link ErrorResponse#MISSING_PERMISSIONS ErrorResponse.MISSING_PERMISSIONS} is increased significantly,
     * otherwise JDA will check permissions and cancel the execution using
     * {@link InsufficientPermissionException InsufficientPermissionException}.
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
