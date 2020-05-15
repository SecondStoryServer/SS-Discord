

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.api.entities.PermissionOverride;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;


public interface PermOverrideManager extends Manager<PermOverrideManager>
{

    long DENIED      = 0x1;

    long ALLOWED     = 0x2;

    long PERMISSIONS = 0x3;


    @Nonnull
    @Override
    PermOverrideManager reset(long fields);


    @Nonnull
    @Override
    PermOverrideManager reset(long... fields);


    @Nonnull
    default Guild getGuild()
    {
        return getPermissionOverride().getGuild();
    }


    @Nonnull
    default GuildChannel getChannel()
    {
        return getPermissionOverride().getChannel();
    }


    @Nonnull
    PermissionOverride getPermissionOverride();


    @Nonnull
    @CheckReturnValue
    PermOverrideManager grant(long permissions);


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager grant(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return grant(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager grant(@Nonnull Collection<Permission> permissions)
    {
        return grant(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    PermOverrideManager deny(long permissions);


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager deny(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return deny(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager deny(@Nonnull Collection<Permission> permissions)
    {
        return deny(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    PermOverrideManager clear(long permissions);


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager clear(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return clear(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default PermOverrideManager clear(@Nonnull Collection<Permission> permissions)
    {
        return clear(Permission.getRaw(permissions));
    }
}
