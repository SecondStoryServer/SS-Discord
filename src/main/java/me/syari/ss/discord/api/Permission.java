
package me.syari.ss.discord.api;

import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;


public enum Permission
{
    CREATE_INSTANT_INVITE(0, true, true, "Create Instant Invite"),
    KICK_MEMBERS(         1, true, false, "Kick Members"),
    BAN_MEMBERS(          2, true, false, "Ban Members"),
    ADMINISTRATOR(        3, true, false, "Administrator"),
    MANAGE_CHANNEL(       4, true, true, "Manage Channels"),
    MANAGE_SERVER(        5, true, false, "Manage Server"),
    MESSAGE_ADD_REACTION( 6, true, true, "Add Reactions"),
    VIEW_AUDIT_LOGS(      7, true, false, "View Audit Logs"),
    PRIORITY_SPEAKER(     8, true, true, "Priority Speaker"),

    // Applicable to all channel types
    VIEW_CHANNEL(            10, true, true, "Read Text Channels & See Voice Channels"),

    // Text Permissions
    MESSAGE_READ(            10, true, true, "Read Messages"),
    MESSAGE_WRITE(           11, true, true, "Send Messages"),
    MESSAGE_TTS(             12, true, true, "Send TTS Messages"),
    MESSAGE_MANAGE(          13, true, true, "Manage Messages"),
    MESSAGE_EMBED_LINKS(     14, true, true, "Embed Links"),
    MESSAGE_ATTACH_FILES(    15, true, true, "Attach Files"),
    MESSAGE_HISTORY(         16, true, true, "Read History"),
    MESSAGE_MENTION_EVERYONE(17, true, true, "Mention Everyone"),
    MESSAGE_EXT_EMOJI(       18, true, true, "Use External Emojis"),

    // Voice Permissions
    VOICE_STREAM(      9, true, true, "Stream"),
    VOICE_CONNECT(    20, true, true, "Connect"),
    VOICE_SPEAK(      21, true, true, "Speak"),
    VOICE_MUTE_OTHERS(22, true, true, "Mute Members"),
    VOICE_DEAF_OTHERS(23, true, true, "Deafen Members"),
    VOICE_MOVE_OTHERS(24, true, true, "Move Members"),
    VOICE_USE_VAD(    25, true, true, "Use Voice Activity"),

    NICKNAME_CHANGE(26, true, false, "Change Nickname"),
    NICKNAME_MANAGE(27, true, false, "Manage Nicknames"),

    MANAGE_ROLES(      28, true, false, "Manage Roles"),
    MANAGE_PERMISSIONS(28, false, true, "Manage Permissions"),
    MANAGE_WEBHOOKS(   29, true, true, "Manage Webhooks"),
    MANAGE_EMOTES(     30, true, false, "Manage Emojis"),

    UNKNOWN(-1, false, false, "Unknown");


    // This is an optimization suggested by Effective Java 3rd Edition - Item 54
    public static final Permission[] EMPTY_PERMISSIONS = new Permission[0];


    public static final long ALL_PERMISSIONS = Permission.getRaw(Permission.values());


    public static final long ALL_CHANNEL_PERMISSIONS = Permission.getRaw(Arrays.stream(values())
            .filter(Permission::isChannel).collect(Collectors.toSet()));


    public static final long ALL_TEXT_PERMISSIONS
            = Permission.getRaw(MESSAGE_ADD_REACTION, MESSAGE_WRITE, MESSAGE_TTS,
                                MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES,
                                MESSAGE_HISTORY, MESSAGE_MENTION_EVERYONE);


    public static final long ALL_VOICE_PERMISSIONS
            = Permission.getRaw(VOICE_STREAM, VOICE_CONNECT, VOICE_SPEAK, VOICE_MUTE_OTHERS,
                                VOICE_DEAF_OTHERS, VOICE_MOVE_OTHERS, VOICE_USE_VAD);

    private final long raw;
    private final boolean isGuild, isChannel;
    private final String name;

    Permission(int offset, boolean isGuild, boolean isChannel, @Nonnull String name)
    {
        this.raw = 1 << offset;
        this.isGuild = isGuild;
        this.isChannel = isChannel;
        this.name = name;
    }


    @Nonnull
    public String getName()
    {
        return this.name;
    }


    public long getRawValue()
    {
        return raw;
    }


    public boolean isGuild()
    {
        return isGuild;
    }


    public boolean isChannel()
    {
        return isChannel;
    }


    public boolean isText()
    {
        return (raw & ALL_TEXT_PERMISSIONS) == raw;
    }


    public boolean isVoice()
    {
        return (raw & ALL_VOICE_PERMISSIONS) == raw;
    }


    @Nonnull
    public static EnumSet<Permission> getPermissions(long permissions)
    {
        if (permissions == 0)
            return EnumSet.noneOf(Permission.class);
        EnumSet<Permission> perms = EnumSet.noneOf(Permission.class);
        for (Permission perm : Permission.values())
        {
            if (perm != UNKNOWN && (permissions & perm.raw) == perm.raw)
                perms.add(perm);
        }
        return perms;
    }


    public static long getRaw(@Nonnull Permission... permissions)
    {
        long raw = 0;
        for (Permission perm : permissions)
        {
            if (perm != null && perm != UNKNOWN)
                raw |= perm.raw;
        }

        return raw;
    }


    public static long getRaw(@Nonnull Collection<Permission> permissions)
    {
        Checks.notNull(permissions, "Permission Collection");

        return getRaw(permissions.toArray(EMPTY_PERMISSIONS));
    }
}
