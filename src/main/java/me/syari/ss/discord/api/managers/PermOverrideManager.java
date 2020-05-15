

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildChannel;
import me.syari.ss.discord.api.entities.PermissionOverride;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Manager providing functionality to update one or more fields for a {@link PermissionOverride PermissionOverride}.
 *
 * <p><b>Example</b>
 * <pre>{@code
 * manager.setDenied(Permission.MESSAGE_WRITE)
 *        .setAllowed(Permission.MESSAGE_READ)
 *        .queue();
 * manager.reset(PermOverrideManager.DENIED | PermOverrideManager.ALLOWED)
 *        .grant(Permission.MESSAGE_WRITE)
 *        .clear(Permission.MESSAGE_MANAGE)
 *        .queue();
 * }</pre>
 *
 * @see PermissionOverride#getManager()
 */
public interface PermOverrideManager extends Manager<PermOverrideManager>
{

    long DENIED      = 0x1;

    long ALLOWED     = 0x2;

    long PERMISSIONS = 0x3;

    /**
     * Resets the fields specified by the provided bit-flag pattern.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(PermOverrideManager.ALLOWED | PermOverrideManager.DENIED);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #DENIED}</li>
     *     <li>{@link #ALLOWED}</li>
     *     <li>{@link #PERMISSIONS}</li>
     * </ul>
     *
     * @param  fields
     *         Integer value containing the flags to reset.
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @Override
    PermOverrideManager reset(long fields);

    /**
     * Resets the fields specified by the provided bit-flag patterns.
     * You can specify a combination by using a bitwise OR concat of the flag constants.
     * <br>Example: {@code manager.reset(PermOverrideManager.ALLOWED, PermOverrideManager.DENIED);}
     *
     * <p><b>Flag Constants:</b>
     * <ul>
     *     <li>{@link #DENIED}</li>
     *     <li>{@link #ALLOWED}</li>
     *     <li>{@link #PERMISSIONS}</li>
     * </ul>
     *
     * @param  fields
     *         Integer values containing the flags to reset.
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @Override
    PermOverrideManager reset(long... fields);

    /**
     * The {@link Guild Guild} this Manager's
     * {@link GuildChannel GuildChannel} is in.
     * <br>This is logically the same as calling {@code getPermissionOverride().getGuild()}
     *
     * @return The parent {@link Guild Guild}
     */
    @Nonnull
    default Guild getGuild()
    {
        return getPermissionOverride().getGuild();
    }

    /**
     * The {@link GuildChannel GuildChannel} this Manager's
     * {@link PermissionOverride PermissionOverride} is in.
     * <br>This is logically the same as calling {@code getPermissionOverride().getChannel()}
     *
     * @return The parent {@link GuildChannel GuildChannel}
     */
    @Nonnull
    default GuildChannel getChannel()
    {
        return getPermissionOverride().getChannel();
    }

    /**
     * The target {@link PermissionOverride PermissionOverride}
     * that will be modified by this Manager
     *
     * @return The target {@link PermissionOverride PermissionOverride}
     */
    @Nonnull
    PermissionOverride getPermissionOverride();

    /**
     * Grants the provided {@link Permission Permissions} bits
     * to the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to grant to the selected {@link PermissionOverride PermissionOverride}
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    PermOverrideManager grant(long permissions);

    /**
     * Grants the provided {@link Permission Permissions}
     * to the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to grant to the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     *
     * @see    Permission#getRaw(Permission...) Permission.getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager grant(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return grant(Permission.getRaw(permissions));
    }

    /**
     * Grants the provided {@link Permission Permissions}
     * to the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to grant to the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     *
     * @see    java.util.EnumSet EnumSet
     * @see    Permission#getRaw(java.util.Collection) Permission.getRaw(Collection)
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager grant(@Nonnull Collection<Permission> permissions)
    {
        return grant(Permission.getRaw(permissions));
    }

    /**
     * Denies the provided {@link Permission Permissions} bits
     * from the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to deny from the selected {@link PermissionOverride PermissionOverride}
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    PermOverrideManager deny(long permissions);

    /**
     * Denies the provided {@link Permission Permissions}
     * from the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to deny from the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     *
     * @see    Permission#getRaw(Permission...) Permission.getRaw(Permission...)
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager deny(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return deny(Permission.getRaw(permissions));
    }

    /**
     * Denies the provided {@link Permission Permissions}
     * from the selected {@link PermissionOverride PermissionOverride}.
     *
     * @param  permissions
     *         The permissions to deny from the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     *
     * @see    java.util.EnumSet EnumSet
     * @see    Permission#getRaw(java.util.Collection) Permission.getRaw(Collection)
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager deny(@Nonnull Collection<Permission> permissions)
    {
        return deny(Permission.getRaw(permissions));
    }

    /**
     * Clears the provided {@link Permission Permissions} bits
     * from the selected {@link PermissionOverride PermissionOverride}.
     * <br>This will cause the provided Permissions to be inherited
     *
     * @param  permissions
     *         The permissions to clear from the selected {@link PermissionOverride PermissionOverride}
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    PermOverrideManager clear(long permissions);

    /**
     * Clears the provided {@link Permission Permissions} bits
     * from the selected {@link PermissionOverride PermissionOverride}.
     * <br>This will cause the provided Permissions to be inherited
     *
     * @param  permissions
     *         The permissions to clear from the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager clear(@Nonnull Permission... permissions)
    {
        Checks.notNull(permissions, "Permissions");
        return clear(Permission.getRaw(permissions));
    }

    /**
     * Clears the provided {@link Permission Permissions} bits
     * from the selected {@link PermissionOverride PermissionOverride}.
     * <br>This will cause the provided Permissions to be inherited
     *
     * @param  permissions
     *         The permissions to clear from the selected {@link PermissionOverride PermissionOverride}
     *
     * @throws IllegalArgumentException
     *         If any of the provided Permissions is {@code null}
     *
     * @return PermOverrideManager for chaining convenience
     *
     * @see    java.util.EnumSet EnumSet
     * @see    Permission#getRaw(java.util.Collection) Permission.getRaw(Collection)
     */
    @Nonnull
    @CheckReturnValue
    default PermOverrideManager clear(@Nonnull Collection<Permission> permissions)
    {
        return clear(Permission.getRaw(permissions));
    }
}
