

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;


public interface RoleManager extends Manager<RoleManager>
{

    long NAME        = 0x1;

    long COLOR       = 0x2;

    long PERMISSION  = 0x4;

    long HOIST       = 0x8;

    long MENTIONABLE = 0x10;


    @Nonnull
    @Override
    RoleManager reset(long fields);


    @Nonnull
    @Override
    RoleManager reset(long... fields);


    @Nonnull
    Role getRole();


    @Nonnull
    default Guild getGuild()
    {
        return getRole().getGuild();
    }


    @Nonnull
    @CheckReturnValue
    RoleManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    RoleManager setPermissions(long perms);


    @Nonnull
    @CheckReturnValue
    default RoleManager setPermissions(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return setPermissions(Arrays.asList(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default RoleManager setPermissions(@Nonnull Collection<Permission> permissions)
    {
        Checks.noneNull(permissions, "Permissions");
        return setPermissions(Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default RoleManager setColor(@Nullable Color color)
    {
        return setColor(color == null ? Role.DEFAULT_COLOR_RAW : color.getRGB());
    }


    @Nonnull
    @CheckReturnValue
    RoleManager setColor(int rgb);


    @Nonnull
    @CheckReturnValue
    RoleManager setHoisted(boolean hoisted);


    @Nonnull
    @CheckReturnValue
    RoleManager setMentionable(boolean mentionable);


    @Nonnull
    @CheckReturnValue
    default RoleManager givePermissions(@Nonnull Permission... perms)
    {
        Checks.notNull(perms, "Permissions");
        return givePermissions(Arrays.asList(perms));
    }


    @Nonnull
    @CheckReturnValue
    RoleManager givePermissions(@Nonnull Collection<Permission> perms);


    @Nonnull
    @CheckReturnValue
    default RoleManager revokePermissions(@Nonnull Permission... perms)
    {
        Checks.notNull(perms, "Permissions");
        return revokePermissions(Arrays.asList(perms));
    }


    @Nonnull
    @CheckReturnValue
    RoleManager revokePermissions(@Nonnull Collection<Permission> perms);
}
