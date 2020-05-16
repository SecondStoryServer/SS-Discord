package me.syari.ss.discord.api;

import javax.annotation.Nonnull;


public enum Permission {
    CREATE_INSTANT_INVITE(true, true, "Create Instant Invite"),
    KICK_MEMBERS(true, false, "Kick Members"),
    BAN_MEMBERS(true, false, "Ban Members"),
    ADMINISTRATOR(true, false, "Administrator"),
    MANAGE_CHANNEL(true, true, "Manage Channels"),
    MANAGE_SERVER(true, false, "Manage Server"),
    MESSAGE_ADD_REACTION(true, true, "Add Reactions"),
    VIEW_AUDIT_LOGS(true, false, "View Audit Logs"),
    PRIORITY_SPEAKER(true, true, "Priority Speaker"),

    // Applicable to all channel types
    VIEW_CHANNEL(true, true, "Read Text Channels & See Voice Channels"),

    // Text Permissions
    MESSAGE_READ(true, true, "Read Messages"),
    MESSAGE_WRITE(true, true, "Send Messages"),
    MESSAGE_TTS(true, true, "Send TTS Messages"),
    MESSAGE_MANAGE(true, true, "Manage Messages"),
    MESSAGE_EMBED_LINKS(true, true, "Embed Links"),
    MESSAGE_ATTACH_FILES(true, true, "Attach Files"),
    MESSAGE_HISTORY(true, true, "Read History"),
    MESSAGE_MENTION_EVERYONE(true, true, "Mention Everyone"),
    MESSAGE_EXT_EMOJI(true, true, "Use External Emojis"),

    // Voice Permissions
    VOICE_STREAM(true, true, "Stream"),
    VOICE_CONNECT(true, true, "Connect"),
    VOICE_SPEAK(true, true, "Speak"),
    VOICE_MUTE_OTHERS(true, true, "Mute Members"),
    VOICE_DEAF_OTHERS(true, true, "Deafen Members"),
    VOICE_MOVE_OTHERS(true, true, "Move Members"),
    VOICE_USE_VAD(true, true, "Use Voice Activity"),

    NICKNAME_CHANGE(true, false, "Change Nickname"),
    NICKNAME_MANAGE(true, false, "Manage Nicknames"),

    MANAGE_ROLES(true, false, "Manage Roles"),
    MANAGE_PERMISSIONS(false, true, "Manage Permissions"),
    MANAGE_WEBHOOKS(true, true, "Manage Webhooks"),
    MANAGE_EMOTES(true, false, "Manage Emojis"),

    UNKNOWN(false, false, "Unknown");


    private final boolean isGuild, isChannel;
    private final String name;

    Permission(boolean isGuild, boolean isChannel, @Nonnull String name) {
        this.isGuild = isGuild;
        this.isChannel = isChannel;
        this.name = name;
    }


    @Nonnull
    public String getName() {
        return this.name;
    }


    public boolean isGuild() {
        return isGuild;
    }


    public boolean isChannel() {
        return isChannel;
    }


}
