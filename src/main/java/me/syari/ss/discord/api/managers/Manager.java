

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.internal.managers.ManagerBase;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BooleanSupplier;

public interface Manager<M extends Manager<M>> extends AuditableRestAction<Void>
{

    static void setPermissionChecksEnabled(boolean enable)
    {
        ManagerBase.setPermissionChecksEnabled(enable);
    }


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


    @Nonnull
    @CheckReturnValue
    M reset();
}
