

package me.syari.ss.discord.internal.utils.config.sharding;

import me.syari.ss.discord.api.entities.Activity;
import me.syari.ss.discord.api.OnlineStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntFunction;

public class PresenceProviderConfig
{
    private IntFunction<? extends Activity> activityProvider;
    private IntFunction<OnlineStatus> statusProvider;
    private IntFunction<Boolean> idleProvider;

    @Nullable
    public IntFunction<? extends Activity> getActivityProvider()
    {
        return activityProvider;
    }

    public void setActivityProvider(@Nullable IntFunction<? extends Activity> activityProvider)
    {
        this.activityProvider = activityProvider;
    }

    @Nullable
    public IntFunction<OnlineStatus> getStatusProvider()
    {
        return statusProvider;
    }

    public void setStatusProvider(@Nullable IntFunction<OnlineStatus> statusProvider)
    {
        this.statusProvider = statusProvider;
    }

    @Nullable
    public IntFunction<Boolean> getIdleProvider()
    {
        return idleProvider;
    }

    public void setIdleProvider(@Nullable IntFunction<Boolean> idleProvider)
    {
        this.idleProvider = idleProvider;
    }

    @Nonnull
    public static PresenceProviderConfig getDefault()
    {
        return new PresenceProviderConfig();
    }
}
