
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.PermissionOverrideAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Represents the specific {@link Member Member} or {@link Role Role}
 * permission overrides that can be set for channels.
 *
 * @see GuildChannel#upsertPermissionOverride(IPermissionHolder)
 * @see GuildChannel#createPermissionOverride(IPermissionHolder)
 * @see GuildChannel#putPermissionOverride(IPermissionHolder)
 *
 * @see GuildChannel#getPermissionOverrides()
 * @see GuildChannel#getPermissionOverride(IPermissionHolder)
 * @see GuildChannel#getMemberPermissionOverrides()
 * @see GuildChannel#getRolePermissionOverrides()
 */
public interface PermissionOverride extends ISnowflake
{
    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>allowed</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the allowed permissions of this override.
     */
    long getAllowedRaw();

    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>not affected</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the unaffected permissions of this override.
     */
    long getInheritRaw();

    /**
     * This is the raw binary representation (as a base 10 long) of the permissions <b>denied</b> by this override.
     * <br>The long relates to the offsets used by each {@link Permission Permission}.
     *
     * @return Never-negative long containing the binary representation of the denied permissions of this override.
     */
    long getDeniedRaw();

    /**
     * EnumSet of all {@link Permission Permissions} that are specifically allowed by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of allowed {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getAllowed();

    /**
     * EnumSet of all {@link Permission Permission} that are unaffected by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of unaffected {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getInherit();

    /**
     * EnumSet of all {@link Permission Permissions} that are denied by this override.
     * <br><u>Changes to the returned set do not affect this entity directly.</u>
     *
     * @return Possibly-empty set of denied {@link Permission Permissions}.
     */
    @Nonnull
    EnumSet<Permission> getDenied();

    /**
     * The {@link JDA JDA} instance that this {@link PermissionOverride PermissionOverride}
     * is related to.
     *
     * @return Never-null {@link JDA JDA} instance.
     */
    @Nonnull
    JDA getJDA();

    /**
     * If this {@link PermissionOverride PermissionOverride} is an override dealing with
     * a {@link Member Member}, then this method will return the related {@link Member Member}.
     * <br>Otherwise, this method returns {@code null}.
     * <br>Basically: if {@link PermissionOverride#isMemberOverride()}
     * returns {@code false}, this returns {@code null}.
     *
     * @return Possibly-null related {@link Member Member}.
     */
    @Nullable
    Member getMember();

    /**
     * If this {@link PermissionOverride PermissionOverride} is an override dealing with
     * a {@link Role Role}, then this method will return the related {@link Role Role}.
     * <br>Otherwise, this method returns {@code null}.
     * Basically: if {@link PermissionOverride#isRoleOverride()}
     * returns {@code false}, this returns {@code null}.
     *
     * @return Possibly-null related {@link Role}.
     */
    @Nullable
    Role getRole();

    /**
     * The {@link GuildChannel GuildChannel} that this {@link PermissionOverride PermissionOverride} affects.
     *
     * @return Never-null related {@link GuildChannel GuildChannel} that this override is part of.
     */
    @Nonnull
    GuildChannel getChannel();

    /**
     * The {@link Guild Guild} that the {@link GuildChannel GuildChannel}
     * returned from {@link PermissionOverride#getChannel()} is a part of.
     * By inference, this is the {@link Guild Guild} that this
     * {@link PermissionOverride PermissionOverride} is part of.
     *
     * @return Never-null related {@link Guild Guild}.
     */
    @Nonnull
    Guild getGuild();

    /**
     * Used to determine if this {@link PermissionOverride PermissionOverride} relates to
     * a specific {@link Member Member}.
     *
     * @return True if this override is a user override.
     */
    boolean isMemberOverride();

    /**
     * Used to determine if this {@link PermissionOverride PermissionOverride} relates to
     * a specific {@link Role Role}.
     *
     * @return True if this override is a role override.
     */
    boolean isRoleOverride();

    /**
     * Returns the {@link PermissionOverrideAction PermissionOverrideAction} to modify this PermissionOverride.
     * <br>In the PermissionOverrideAction you can modify the permissions of the override.
     * You modify multiple fields in one request by chaining setters before calling {@link RestAction#queue() RestAction.queue()}.
     *
     * @throws InsufficientPermissionException
     *         If the currently logged in account does not have {@link Permission#MANAGE_PERMISSIONS Permission.MANAGE_PERMISSIONS}
     *
     * @return The PermissionOverrideAction of this override.
     */
    @Nonnull
    PermissionOverrideAction getManager();

    /**
     * Deletes this PermissionOverride.
     *
     * <p>Possible ErrorResponses include:
     * <ul>
     *     <li>{@link ErrorResponse#UNKNOWN_OVERRIDE}
     *     <br>If the the override was already deleted.</li>
     *
     *     <li>{@link ErrorResponse#UNKNOWN_CHANNEL UNKNOWN_CHANNEL}
     *     <br>If the channel this override was a part of was already deleted</li>
     *
     *     <li>{@link ErrorResponse#MISSING_ACCESS MISSING_ACCESS}
     *     <br>If we were removed from the Guild</li>
     * </ul>
     *
     * @throws InsufficientPermissionException
     *         if we don't have the permission to {@link Permission#MANAGE_CHANNEL MANAGE_CHANNEL}
     *
     * @return {@link AuditableRestAction AuditableRestAction}
     */
    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();
}
