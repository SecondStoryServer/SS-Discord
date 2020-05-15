

package me.syari.ss.discord.api.requests.restaction;

import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collection;
import java.util.function.BooleanSupplier;

/**
 * Extension of {@link RestAction RestAction} specifically
 * designed to create a {@link Role Role}.
 * This extension allows setting properties before executing the action.
 *
 * @since  3.0
 *
 * @see    Guild
 * @see    Guild#createRole()
 * @see    Role#createCopy()
 * @see    Role#createCopy(Guild)
 */
public interface RoleAction extends AuditableRestAction<Role>
{
    @Nonnull
    @Override
    RoleAction setCheck(@Nullable BooleanSupplier checks);

    /**
     * The guild to create the role in
     *
     * @return The guild
     */
    @Nonnull
    Guild getGuild();

    /**
     * Sets the name for new role (optional)
     *
     * @param  name
     *         The name for the new role, null to use default name
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided name is longer than 100 characters
     *
     * @return The current RoleAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    RoleAction setName(@Nullable String name);

    /**
     * Sets whether or not the new role should be hoisted
     *
     * @param  hoisted
     *         Whether the new role should be hoisted (grouped). Default is {@code false}
     *
     * @return The current RoleAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    RoleAction setHoisted(@Nullable Boolean hoisted);

    /**
     * Sets whether the new role should be mentionable by members of
     * the parent {@link Guild Guild}.
     *
     * @param  mentionable
     *         Whether the new role should be mentionable. Default is {@code false}
     *
     * @return The current RoleAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    RoleAction setMentionable(@Nullable Boolean mentionable);

    /**
     * Sets the color which the new role should be displayed with.
     *
     * @param  color
     *         An {@link java.awt.Color Color} for the new role, null to use default white/black
     *
     * @return The current RoleAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    default RoleAction setColor(@Nullable Color color)
    {
        return this.setColor(color != null ? color.getRGB() : null);
    }

    /**
     * Sets the Color for the new role.
     * This accepts colors from the range {@code 0x000} to {@code 0xFFFFFF}.
     * The provided value will be ranged using {@code rbg & 0xFFFFFF}
     *
     * @param  rgb
     *         The color for the new role in integer form, {@code null} to use default white/black
     *
     * @return The current RoleAction, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    RoleAction setColor(@Nullable Integer rgb);

    /**
     * Sets the Permissions the new Role should have.
     * This will only allow permissions that the current account already holds unless
     * the account is owner or {@link Permission#ADMINISTRATOR admin} of the parent {@link Guild Guild}.
     *
     * @param  permissions
     *         The varargs {@link Permission Permissions} for the new role
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not hold one of the specified permissions
     * @throws IllegalArgumentException
     *         If any of the provided permissions is {@code null}
     *
     * @return The current RoleAction, for chaining convenience
     *
     * @see    Permission#getRaw(Permission...) Permission.getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    default RoleAction setPermissions(@Nullable Permission... permissions)
    {
        if (permissions != null)
            Checks.noneNull(permissions, "Permissions");

        return setPermissions(permissions == null ? null : Permission.getRaw(permissions));
    }

    /**
     * Sets the Permissions the new Role should have.
     * This will only allow permissions that the current account already holds unless
     * the account is owner or {@link Permission#ADMINISTRATOR admin} of the parent {@link Guild Guild}.
     *
     * @param  permissions
     *         A {@link java.util.Collection Collection} of {@link Permission Permissions} for the new role
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not hold one of the specified permissions
     * @throws IllegalArgumentException
     *         If any of the provided permissions is {@code null}
     *
     * @return The current RoleAction, for chaining convenience
     *
     * @see    Permission#getRaw(java.util.Collection) Permission.getRaw(Collection)
     * @see    java.util.EnumSet EnumSet
     */
    @Nonnull
    @CheckReturnValue
    default RoleAction setPermissions(@Nullable Collection<Permission> permissions)
    {
        if (permissions != null)
            Checks.noneNull(permissions, "Permissions");

        return setPermissions(permissions == null ? null : Permission.getRaw(permissions));
    }

    /**
     * Sets the Permissions the new Role should have.
     * This will only allow permissions that the current account already holds unless
     * the account is owner or {@link Permission#ADMINISTRATOR admin} of the parent {@link Guild Guild}.
     *
     * @param  permissions
     *         The raw {@link Permission Permissions} value for the new role.
     *         To retrieve this use {@link Permission#getRawValue()}
     *
     * @throws java.lang.IllegalArgumentException
     *         If the provided permission value is invalid
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not hold one of the specified permissions
     *
     * @return The current RoleAction, for chaining convenience
     *
     * @see    Permission#getRawValue()
     * @see    Permission#getRaw(java.util.Collection)
     * @see    Permission#getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    RoleAction setPermissions(@Nullable Long permissions);
}
