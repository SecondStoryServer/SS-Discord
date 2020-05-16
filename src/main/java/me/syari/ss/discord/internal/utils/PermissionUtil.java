package me.syari.ss.discord.internal.utils;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.internal.entities.GuildImpl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PermissionUtil {


    public static boolean canInteract(Member issuer, Role target) {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(target, "Target Role");

        Guild guild = issuer.getGuild();
        if (!guild.equals(target.getGuild()))
            throw new IllegalArgumentException("Provided Member issuer and Role target must be from the same Guild!");
        if (issuer.isOwner())
            return true;
        List<Role> issuerRoles = issuer.getRoles();
        return !issuerRoles.isEmpty() && canInteract(issuerRoles.get(0), target);
    }


    public static boolean canInteract(Role issuer, Role target) {
        Checks.notNull(issuer, "Issuer Role");
        Checks.notNull(target, "Target Role");

        if (!issuer.getGuild().equals(target.getGuild()))
            throw new IllegalArgumentException("The 2 Roles are not from same Guild!");
        return target.getPosition() < issuer.getPosition();
    }


    public static boolean checkPermission(Member member, Permission... permissions) {
        Checks.notNull(member, "Member");
        Checks.notNull(permissions, "Permissions");

        long effectivePerms = getEffectivePermission(member);
        return isApplied(effectivePerms, Permission.ADMINISTRATOR.getRawValue())
                || isApplied(effectivePerms, Permission.getRaw(permissions));
    }


    public static boolean checkPermission(GuildChannel channel, Member member, Permission... permissions) {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");
        Checks.notNull(permissions, "Permissions");

        GuildImpl guild = (GuildImpl) channel.getGuild();
        checkGuild(guild, member.getGuild(), "Member");

        long effectivePerms = getEffectivePermission(channel, member);
        return isApplied(effectivePerms, Permission.getRaw(permissions));
    }


    public static long getEffectivePermission(Member member) {
        Checks.notNull(member, "Member");

        if (member.isOwner())
            return Permission.ALL_PERMISSIONS;
        //Default to binary OR of all global permissions in this guild
        long permission = member.getGuild().getPublicRole().getPermissionsRaw();
        for (Role role : member.getRoles()) {
            permission |= role.getPermissionsRaw();
            if (isApplied(permission, Permission.ADMINISTRATOR.getRawValue()))
                return Permission.ALL_PERMISSIONS;
        }

        return permission;
    }


    public static long getEffectivePermission(GuildChannel channel, Member member) {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");

        Checks.check(channel.getGuild().equals(member.getGuild()), "Provided channel and provided member are not of the same guild!");

        if (member.isOwner()) {
            // Owner effectively has all permissions
            return Permission.ALL_PERMISSIONS;
        }

        long permission = getEffectivePermission(member);
        final long admin = Permission.ADMINISTRATOR.getRawValue();
        if (isApplied(permission, admin))
            return Permission.ALL_PERMISSIONS;

        AtomicLong allow = new AtomicLong(0);
        AtomicLong deny = new AtomicLong(0);
        getExplicitOverrides(channel, member, allow, deny);
        permission = apply(permission, allow.get(), deny.get());
        final long viewChannel = Permission.VIEW_CHANNEL.getRawValue();

        //When the permission to view the channel is not applied it is not granted
        // This means that we have no access to this channel at all
        return isApplied(permission, viewChannel) ? permission : 0;
        /*
        // currently discord doesn't implicitly grant permissions that the user can grant others
        // so instead the user has to explicitly make an override to grant them the permission in order to be granted that permission
        // yes this makes no sense but what can i do, the devs don't like changing things apparently...
        // I've been told half a year ago this would be changed but nothing happens
        // so instead I'll just bend over for them so people get "correct" permission checks...
        //
        // only time will tell if something happens and I can finally re-implement this section wew
        final long managePerms = Permission.MANAGE_PERMISSIONS.getRawValue();
        final long manageChannel = Permission.MANAGE_CHANNEL.getRawValue();
        if ((permission & (managePerms | manageChannel)) != 0)
        {
            // In channels, MANAGE_CHANNEL and MANAGE_PERMISSIONS grant full text/voice permissions
            permission |= Permission.ALL_TEXT_PERMISSIONS | Permission.ALL_VOICE_PERMISSIONS;
        }
        */
    }


    public static long getEffectivePermission(GuildChannel channel, Role role) {
        Checks.notNull(channel, "Channel");
        Checks.notNull(role, "Role");

        Guild guild = channel.getGuild();
        if (!guild.equals(role.getGuild()))
            throw new IllegalArgumentException("Provided channel and role are not of the same guild!");

        long permissions = getExplicitPermission(channel, role);
        if (isApplied(permissions, Permission.ADMINISTRATOR.getRawValue()))
            return Permission.ALL_CHANNEL_PERMISSIONS;
        else if (!isApplied(permissions, Permission.VIEW_CHANNEL.getRawValue()))
            return 0;
        return permissions;
    }


    public static long getExplicitPermission(GuildChannel channel, Role role) {
        Checks.notNull(channel, "Channel");
        Checks.notNull(role, "Role");

        final Guild guild = role.getGuild();
        checkGuild(channel.getGuild(), guild, "Role");

        long permission = role.getPermissionsRaw() | guild.getPublicRole().getPermissionsRaw();
        PermissionOverride override = channel.getPermissionOverride(guild.getPublicRole());
        if (override != null)
            permission = apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
        if (role.isPublicRole())
            return permission;

        override = channel.getPermissionOverride(role);

        return override == null
                ? permission
                : apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
    }

    private static void getExplicitOverrides(GuildChannel channel, Member member, AtomicLong allow, AtomicLong deny) {
        PermissionOverride override = channel.getPermissionOverride(member.getGuild().getPublicRole());
        long allowRaw = 0;
        long denyRaw = 0;
        if (override != null) {
            denyRaw = override.getDeniedRaw();
            allowRaw = override.getAllowedRaw();
        }

        long allowRole = 0;
        long denyRole = 0;
        // create temporary bit containers for role cascade
        for (Role role : member.getRoles()) {
            override = channel.getPermissionOverride(role);
            if (override != null) {
                // important to update role cascade not others
                denyRole |= override.getDeniedRaw();
                allowRole |= override.getAllowedRaw();
            }
        }
        // Override the raw values of public role then apply role cascade
        allowRaw = (allowRaw & ~denyRole) | allowRole;
        denyRaw = (denyRaw & ~allowRole) | denyRole;

        override = channel.getPermissionOverride(member);
        if (override != null) {
            // finally override the role cascade with member overrides
            final long oDeny = override.getDeniedRaw();
            final long oAllow = override.getAllowedRaw();
            allowRaw = (allowRaw & ~oDeny) | oAllow;
            denyRaw = (denyRaw & ~oAllow) | oDeny;
            // this time we need to exclude new allowed bits from old denied ones and OR the new denied bits as final overrides
        }
        // set as resulting values
        allow.set(allowRaw);
        deny.set(denyRaw);
    }

    /*
     * Check whether the specified permission is applied in the bits
     */
    private static boolean isApplied(long permissions, long perms) {
        return (permissions & perms) == perms;
    }

    private static long apply(long permission, long allow, long deny) {
        permission &= ~deny;  //Deny everything that the cascade of roles denied.
        permission |= allow;  //Allow all the things that the cascade of roles allowed
        // The allowed bits override the denied ones!
        return permission;
    }

    private static void checkGuild(Guild o1, Guild o2, String name) {
        Checks.check(o1.equals(o2),
                "Specified %s is not in the same guild! (%s / %s)", name, o1, o2);
    }
}
