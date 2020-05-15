

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.BooleanSupplier;


public interface PermissionOverrideAction extends AuditableRestAction<PermissionOverride>
{
    @Nonnull
    @Override
    PermissionOverrideAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    default PermissionOverrideAction reset()
    {
        return resetAllow().resetDeny();
    }


    @Nonnull
    PermissionOverrideAction resetAllow();


    @Nonnull
    PermissionOverrideAction resetDeny();


    @Nonnull
    GuildChannel getChannel();


    @Nullable
    Role getRole();


    @Nullable
    Member getMember();


    @Nonnull
    default Guild getGuild()
    {
        return getChannel().getGuild();
    }


    long getAllow();


    @Nonnull
    default EnumSet<Permission> getAllowedPermissions()
    {
        return Permission.getPermissions(getAllow());
    }


    long getDeny();


    @Nonnull
    default EnumSet<Permission> getDeniedPermissions()
    {
        return Permission.getPermissions(getDeny());
    }


    long getInherited();


    @Nonnull
    default EnumSet<Permission> getInheritedPermissions()
    {
        return Permission.getPermissions(getInherited());
    }


    boolean isMember();


    boolean isRole();


    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction setAllow(long allowBits);


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction setAllow(@Nullable Collection<Permission> permissions)
    {
        if (permissions == null || permissions.isEmpty())
            return setAllow(0);
        Checks.noneNull(permissions, "Permissions");
        return setAllow(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction setAllow(@Nullable Permission... permissions)
    {
        if (permissions == null || permissions.length == 0)
            return setAllow(0);
        Checks.noneNull(permissions, "Permissions");
        return setAllow(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction grant(long allowBits)
    {
        return setAllow(getAllow() | allowBits);
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction grant(@Nonnull Collection<Permission> permissions)
    {
        return setAllow(getAllow() | Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction grant(@Nonnull Permission... permissions)
    {
        return setAllow(getAllow() | Permission.getRaw(permissions));
    }



    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction setDeny(long denyBits);


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction setDeny(@Nullable Collection<Permission> permissions)
    {
        if (permissions == null || permissions.isEmpty())
            return setDeny(0);
        Checks.noneNull(permissions, "Permissions");
        return setDeny(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction setDeny(@Nullable Permission... permissions)
    {
        if (permissions == null || permissions.length == 0)
            return setDeny(0);
        Checks.noneNull(permissions, "Permissions");
        return setDeny(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction deny(long denyBits)
    {
        return setDeny(getDeny() | denyBits);
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction deny(@Nonnull Collection<Permission> permissions)
    {
        return setDeny(getDeny() | Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction deny(@Nonnull Permission... permissions)
    {
        return setDeny(getDeny() | Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction clear(long inheritedBits)
    {
        return setDeny(getDeny() & ~inheritedBits).setAllow(getAllow() & ~inheritedBits);
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction clear(@Nonnull Collection<Permission> permissions)
    {
        return clear(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction clear(@Nonnull Permission... permissions)
    {
        return clear(Permission.getRaw(permissions));
    }



    @Nonnull
    @CheckReturnValue
    PermissionOverrideAction setPermissions(long allowBits, long denyBits);


    @Nonnull
    @CheckReturnValue
    default PermissionOverrideAction setPermissions(@Nullable Collection<Permission> grantPermissions, @Nullable Collection<Permission> denyPermissions)
    {
        return setAllow(grantPermissions).setDeny(denyPermissions);
    }
}
