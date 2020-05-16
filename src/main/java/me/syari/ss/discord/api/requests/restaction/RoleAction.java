package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.function.BooleanSupplier;


public interface RoleAction extends AuditableRestAction<Role> {
    @Nonnull
    @Override
    RoleAction setCheck(@Nullable BooleanSupplier checks);


    @Nonnull
    Guild getGuild();


    @Nonnull
    @CheckReturnValue
    RoleAction setName(@Nullable String name);


    @Nonnull
    @CheckReturnValue
    RoleAction setHoisted(@Nullable Boolean hoisted);


    @Nonnull
    @CheckReturnValue
    RoleAction setMentionable(@Nullable Boolean mentionable);


    @Nonnull
    @CheckReturnValue
    default RoleAction setColor(@Nullable Color color) {
        return this.setColor(color != null ? color.getRGB() : null);
    }


    @Nonnull
    @CheckReturnValue
    RoleAction setColor(@Nullable Integer rgb);


    @Nonnull
    @CheckReturnValue
    default RoleAction setPermissions(@Nullable Permission... permissions) {
        if (permissions != null)
            Checks.noneNull(permissions, "Permissions");

        return setPermissions(permissions == null ? null : Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    default RoleAction setPermissions(@Nullable Collection<Permission> permissions) {
        if (permissions != null)
            Checks.noneNull(permissions, "Permissions");

        return setPermissions(permissions == null ? null : Permission.getRaw(permissions));
    }


    @Nonnull
    @CheckReturnValue
    RoleAction setPermissions(@Nullable Long permissions);
}
