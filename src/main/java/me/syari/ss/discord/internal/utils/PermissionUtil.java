package me.syari.ss.discord.internal.utils;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.internal.entities.GuildImpl;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PermissionUtil {

    public static boolean canInteract(Member issuer, Member target) {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(target, "Target Member");

        Guild guild = issuer.getGuild();
        if (!guild.equals(target.getGuild()))
            throw new IllegalArgumentException("Provided members must both be Member objects of the same Guild!");
        if (issuer.isOwner())
            return true;
        if (target.isOwner())
            return false;
        List<Role> issuerRoles = issuer.getRoles();
        List<Role> targetRoles = target.getRoles();
        return !issuerRoles.isEmpty() && (targetRoles.isEmpty() || canInteract(issuerRoles.get(0), targetRoles.get(0)));
    }


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


    public static boolean canInteract(Member issuer, Emote emote) {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(emote, "Target Emote");

        if (!issuer.getGuild().equals(emote.getGuild()))
            throw new IllegalArgumentException("The issuer and target are not in the same Guild");

        // We don't need to check based on the fact it is animated if it's a BOT account
        // because BOT accounts cannot have nitro, and have access to animated Emotes naturally.
        if (emote.isAnimated() && !issuer.getUser().isBot()) {
            // This is a currently logged in client, meaning we can check if they have nitro or not.
            // If this isn't the currently logged in account, we just check it like a normal emote,
            // since there is no way to verify if they have nitro or not.
            if (issuer.getUser() instanceof SelfUser) {
                // If they don't have nitro, we immediately return
                // false, otherwise we proceed with the remaining checks.
                return false;
            }
        }

        return emote.canProvideRoles() && (emote.getRoles().isEmpty() // Emote restricted to roles -> check if the issuer has them
                || CollectionUtils.containsAny(issuer.getRoles(), emote.getRoles()));
    }


    public static boolean canInteract(User issuer, Emote emote, MessageChannel channel, boolean botOverride) {
        Checks.notNull(issuer, "Issuer Member");
        Checks.notNull(emote, "Target Emote");
        Checks.notNull(channel, "Target Channel");

        if (emote.getGuild() == null || !emote.getGuild().isMember(issuer))
            return false; // cannot use an emote if you're not in its guild
        Member member = emote.getGuild().getMemberById(issuer.getIdLong());
        if (!canInteract(member, emote))
            return false;
        // external means it is available outside of its own guild - works for bots or if its managed
        // currently we cannot check whether other users have nitro, we assume no here
        final boolean external = emote.isManaged() || (issuer.isBot() && botOverride);
        if (channel.getType() == ChannelType.TEXT) {
            TextChannel text = (TextChannel) channel;
            member = text.getGuild().getMemberById(issuer.getIdLong());
            return emote.getGuild().equals(text.getGuild()) // within the same guild
                    || (external && member != null && member.hasPermission(text, Permission.MESSAGE_EXT_EMOJI)); // in different guild
        }
        return external; // In Group or Private it only needs to be external
    }


    public static boolean canInteract(User issuer, Emote emote, MessageChannel channel) {
        return canInteract(issuer, emote, channel, true);
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


    public static long getExplicitPermission(Member member) {
        Checks.notNull(member, "Member");

        final Guild guild = member.getGuild();
        long permission = guild.getPublicRole().getPermissionsRaw();

        for (Role role : member.getRoles())
            permission |= role.getPermissionsRaw();

        return permission;
    }


    public static long getExplicitPermission(GuildChannel channel, Member member) {
        Checks.notNull(channel, "Channel");
        Checks.notNull(member, "Member");

        final Guild guild = member.getGuild();
        checkGuild(channel.getGuild(), guild, "Member");

        long permission = getExplicitPermission(member);

        AtomicLong allow = new AtomicLong(0);
        AtomicLong deny = new AtomicLong(0);

        // populates allow/deny
        getExplicitOverrides(channel, member, allow, deny);

        return apply(permission, allow.get(), deny.get());
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
