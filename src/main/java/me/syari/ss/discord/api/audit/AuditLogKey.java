

package me.syari.ss.discord.api.audit;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.entities.Guild;


public enum AuditLogKey
{
    
    ID("id"),

    
    TYPE("type"),

    
    APPLICATION_ID("application_id"),

    // GUILD
    
    GUILD_NAME("name"),

    
    GUILD_OWNER("owner_id"),

    
    GUILD_REGION("region"),

    
    GUILD_AFK_TIMEOUT("afk_timeout"),

    
    GUILD_AFK_CHANNEL("afk_channel_id"),

    
    GUILD_SYSTEM_CHANNEL("system_channel_id"),

    
    GUILD_EXPLICIT_CONTENT_FILTER("explicit_content_filter"),

    
    GUILD_ICON("icon_hash"),

    
    GUILD_SPLASH("splash_hash"),

    
    GUILD_VERIFICATION_LEVEL("verification_level"),

    
    GUILD_NOTIFICATION_LEVEL("default_message_notifications"),

    
    GUILD_MFA_LEVEL("mfa_level"),

    
    GUILD_VANITY_URL_CODE("vanity_url_code"),

    
    GUILD_PRUNE_DELETE_DAYS("prune_delete_days"),

    
    GUILD_WIDGET_ENABLED("widget_enabled"),

    
    GUILD_WIDGET_CHANNEL_ID("widget_channel_id"),


    // CHANNEL
    
    CHANNEL_NAME("name"),

    
    CHANNEL_PARENT("parent_id"),

    
    CHANNEL_TOPIC("topic"),

    
    CHANNEL_SLOWMODE("rate_limit_per_user"),

    
    CHANNEL_BITRATE("bitrate"),

    
    CHANNEL_USER_LIMIT("user_limit"),

    
    CHANNEL_NSFW("nsfw"),

    
    CHANNEL_TYPE("type"),

    
    CHANNEL_OVERRIDES("permission_overwrites"),


    // MEMBER
    
    MEMBER_NICK("nick"),

    
    MEMBER_MUTE("mute"),

    
    MEMBER_DEAF("deaf"),

    
    MEMBER_ROLES_ADD("$add"),

    
    MEMBER_ROLES_REMOVE("$remove"),


    // PERMISSION OVERRIDE
    
    OVERRIDE_DENY("deny"),

    
    OVERRIDE_ALLOW("allow"),

    
    OVERRIDE_TYPE("type"),


    // ROLE
    
    ROLE_NAME("name"),

    
    ROLE_PERMISSIONS("permissions"),

    
    ROLE_COLOR("color"),

    
    ROLE_HOISTED("hoist"),

    
    ROLE_MENTIONABLE("mentionable"),


    // EMOTE
    
    EMOTE_NAME("name"),

    
    EMOTE_ROLES_ADD("$add"),

    
    EMOTE_ROLES_REMOVE("$remove"),


    // WEBHOOK
    
    WEBHOOK_NAME("name"),

    
    WEBHOOK_ICON("avatar_hash"),

    
    WEBHOOK_CHANNEL("channel_id"),


    // INVITE
    
    INVITE_CODE("code"),

    
    INVITE_MAX_AGE("max_age"),

    
    INVITE_TEMPORARY("temporary"),

    
    INVITE_INVITER("inviter"),

    
    INVITE_CHANNEL("channel_id"),

    
    INVITE_USES("uses"),

    
    INVITE_MAX_USES("max_uses");


    private final String key;

    AuditLogKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    @Override
    public String toString()
    {
        return name() + '(' + key + ')';
    }
}
