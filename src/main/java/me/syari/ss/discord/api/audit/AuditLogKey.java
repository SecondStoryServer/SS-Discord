

package me.syari.ss.discord.api.audit;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.*;
import me.syari.ss.discord.api.entities.Guild;

/**
 * Enum of possible/expected keys that can be provided
 * to {@link AuditLogEntry#getChangeByKey(AuditLogKey) AuditLogEntry.getChangeByKey(AuditLogEntry.AuditLogKey}.
 *
 * <p>Each constant in this enum has elaborate documentation on expected values for the
 * returned {@link AuditLogChange AuditLogChange}.
 * <br>There is no guarantee that the resulting type is accurate or that the value selected is not {@code null}!
 */
public enum AuditLogKey
{
    /**
     * This is sometimes visible for {@link ActionType ActionTypes}
     * which create a new entity.
     * <br>Use with designated {@code getXById} method.
     *
     * <p>Expected type: <b>String</b>
     */
    ID("id"),

    /**
     * Entity type (like channel type or webhook type)
     *
     * <p>Expected type: <b>String or int</b>
     */
    TYPE("type"),

    /**
     * The id for an authorized application (webhook/bot/integration)
     *
     * <p>Expected type: <b>String</b>
     */
    APPLICATION_ID("application_id"),

    // GUILD
    /**
     * Change for the {@link Guild#getName() Guild.getName()} value
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_NAME("name"),

    /**
     * Change of User ID for the owner of a {@link Guild Guild}
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_OWNER("owner_id"),

    /**
     * Change of region represented by a key.
     * <br>Use with {@link Region#fromKey(String) Region.fromKey(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_REGION("region"),

    /**
     * Change of the {@link Guild.Timeout AFKTimeout} of a Guild.
     * <br>Use with {@link Guild.Timeout#fromKey(int) Timeout.fromKey(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_AFK_TIMEOUT("afk_timeout"),

    /**
     * Change of the {@link Guild#getAfkChannel() Guild.getAfkChannel()} value represented by a VoiceChannel ID.
     * <br>Use with {@link Guild#getVoiceChannelById(String) Guild.getVoiceChannelById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_AFK_CHANNEL("afk_channel_id"),

    /**
     * Change of the {@link Guild#getSystemChannel() Guild.getSystemChannel()} value represented by a TextChannel ID.
     * <br>Use with {@link Guild#getTextChannelById(String) Guild.getTextChannelById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_SYSTEM_CHANNEL("system_channel_id"),

    /**
     * Change of the {@link Guild#getExplicitContentLevel() Guild.getExplicitContentLevel()} of a Guild.
     * <br>Use with {@link Guild.ExplicitContentLevel#fromKey(int) Guild.ExplicitContentLevel.fromKey(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_EXPLICIT_CONTENT_FILTER("explicit_content_filter"),

    /**
     * Change of the {@link Guild#getIconId() Icon ID} of a Guild.
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_ICON("icon_hash"),

    /**
     * Change of the {@link Guild#getSplashId() Splash ID} of a Guild.
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_SPLASH("splash_hash"),

    /**
     * Change of the {@link Guild#getVerificationLevel() Guild.getVerificationLevel()} value.
     * <br>Use with {@link Guild.VerificationLevel#fromKey(int) Guild.VerificationLevel.fromKey(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_VERIFICATION_LEVEL("verification_level"),

    /**
     * Change of the {@link Guild#getDefaultNotificationLevel() Guild.getDefaultNotificationLevel()} value.
     * <br>Use with {@link Guild.NotificationLevel#fromKey(int) Guild.NotificationLevel.fromKey(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_NOTIFICATION_LEVEL("default_message_notifications"),

    /**
     * Change of the {@link Guild#getRequiredMFALevel() Guild.getRequiredMFALevel()} value
     * <br>Use with {@link Guild.MFALevel#fromKey(int) Guild.MFALevel.fromKey(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_MFA_LEVEL("mfa_level"),

    /**
     * Change of the {@link Guild#getVanityCode()} value.
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_VANITY_URL_CODE("vanity_url_code"),

    /**
     * Days of inactivity for a prune event.
     *
     * <p>Expected type: <b>Integer</b>
     */
    GUILD_PRUNE_DELETE_DAYS("prune_delete_days"),

    /**
     * Whether the guild widget is disabled or enabled
     *
     * <p>Expected type: <b>Boolean</b>
     */
    GUILD_WIDGET_ENABLED("widget_enabled"),

    /**
     * The target channel for a widget
     *
     * <p>Expected type: <b>String</b>
     */
    GUILD_WIDGET_CHANNEL_ID("widget_channel_id"),


    // CHANNEL
    /**
     * Change of the {@link GuildChannel#getName() GuildChannel.getName()} value.
     *
     * <p>Expected type: <b>String</b>
     */
    CHANNEL_NAME("name"),

    /**
     * Change of the {@link GuildChannel#getParent() GuildChannel.getParent()} value.
     * <br>Use with {@link Guild#getCategoryById(String) Guild.getCategoryById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    CHANNEL_PARENT("parent_id"),

    /**
     * Change of the {@link TextChannel#getTopic() TextChannel.getTopic()} value.
     * <br>Only for {@link ChannelType#TEXT ChannelType.TEXT}
     *
     * <p>Expected type: <b>String</b>
     */
    CHANNEL_TOPIC("topic"),

    /**
     * Change of the {@link TextChannel#getSlowmode() TextChannel.getSlowmode()} value.
     * <br>Only for {@link ChannelType#TEXT ChannelType.TEXT}
     *
     * <p>Expected type: <b>Integer</b>
     */
    CHANNEL_SLOWMODE("rate_limit_per_user"),

    /**
     * Change of the {@link VoiceChannel#getBitrate() VoiceChannel.getBitrate()} value.
     * <br>Only for {@link ChannelType#VOICE ChannelType.VOICE}
     *
     * <p>Expected type: <b>Integer</b>
     */
    CHANNEL_BITRATE("bitrate"),

    /**
     * Change of the {@link VoiceChannel#getUserLimit() VoiceChannel.getUserLimit()} value.
     * <br>Only for {@link ChannelType#VOICE ChannelType.VOICE}
     *
     * <p>Expected type: <b>Integer</b>
     */
    CHANNEL_USER_LIMIT("user_limit"),

    /**
     * Change of the {@link TextChannel#isNSFW() TextChannel.isNSFW()} value.
     * <br>Only for {@link ChannelType#TEXT ChannelType.TEXT}
     *
     * <p>Expected type: <b>Boolean</b>
     */
    CHANNEL_NSFW("nsfw"),

    /**
     * The integer type of this channel.
     * <br>Use with {@link ChannelType#fromId(int) ChannelType.fromId(int)}.
     *
     * <p>Expected type: <b>int</b>
     */
    CHANNEL_TYPE("type"),

    /**
     * The overrides for this channel.
     *
     * <p>Expected type: <b>List{@literal <Map<String, Object>>}</b>
     */
    CHANNEL_OVERRIDES("permission_overwrites"),


    // MEMBER
    /**
     * Change of the {@link Member#getNickname() Member.getNickname()} value
     *
     * <p>Expected type: <b>String</b>
     */
    MEMBER_NICK("nick"),

    /**
     * Change of the {@link Member#getVoiceState() GuildVoiceState} of a Member.
     * <br>Indicating that the {@link GuildVoiceState#isGuildMuted() Guild.isGuildMuted()} value updated.
     *
     * <p>Expected type: <b>Boolean</b>
     */
    MEMBER_MUTE("mute"),

    /**
     * Change of the {@link Member#getVoiceState() GuildVoiceState} of a Member.
     * <br>Indicating that the {@link GuildVoiceState#isGuildDeafened() Guild.isGuildDeafened()} value updated.
     *
     * <p>Expected type: <b>Boolean</b>
     */
    MEMBER_DEAF("deaf"),

    /**
     * Roles added to {@link Member#getRoles() Member.getRoles()} with this action
     * <br>Containing a list of {@link Role Role} IDs
     * <br>Use with {@link Guild#getRoleById(String) Guild.getRoleById(String)}
     *
     * <p>Expected type: <b>List{@literal <String>}</b>
     */
    MEMBER_ROLES_ADD("$add"),

    /**
     * Roles removed from {@link Member#getRoles() Member.getRoles()} with this action
     * <br>Containing a list of {@link Role Role} IDs
     * <br>Use with {@link Guild#getRoleById(String) Guild.getRoleById(String)}
     *
     * <p>Expected type: <b>List{@literal <String>}</b>
     */
    MEMBER_ROLES_REMOVE("$remove"),


    // PERMISSION OVERRIDE
    /**
     * Modified raw denied permission bits
     * <br>Similar to the value returned by {@link PermissionOverride#getDeniedRaw() PermissionOverride.getDeniedRaw()}
     * <br>Use with {@link Permission#getPermissions(long) Permission.getPermissions(long)}
     *
     * <p>Expected type: <b>long</b>
     */
    OVERRIDE_DENY("deny"),

    /**
     * Modified raw allowed permission bits
     * <br>Similar to the value returned by {@link PermissionOverride#getAllowedRaw() PermissionOverride.getAllowedRaw()}
     * <br>Use with {@link Permission#getPermissions(long) Permission.getPermissions(long)}
     *
     * <p>Expected type: <b>long</b>
     */
    OVERRIDE_ALLOW("allow"),

    /**
     * The string type of this override.
     * <br>{@code "role"} or {@code "member"}.
     *
     * <p>Expected type: <b>String</b>
     */
    OVERRIDE_TYPE("type"),


    // ROLE
    /**
     * Change of the {@link Role#getName() Role.getName()} value.
     *
     * <p>Expected type: <b>String</b>
     */
    ROLE_NAME("name"),

    /**
     * Change of the {@link Role#getPermissionsRaw() Role.getPermissionsRaw()} value.
     * <br>Use with {@link Permission#getPermissions(long) Permission.getPermissions(long)}
     *
     * <p>Expected type: <b>Long</b>
     */
    ROLE_PERMISSIONS("permissions"),

    /**
     * Change of the {@link Role#getColor() Role.getColor()} value.
     * <br>Use with {@link java.awt.Color#Color(int) Color(int)}
     *
     * <p>Expected type: <b>Integer</b>
     */
    ROLE_COLOR("color"),

    /**
     * Change of the {@link Role#isHoisted() Role.isHoisted()} value.
     *
     * <p>Expected type: <b>Boolean</b>
     */
    ROLE_HOISTED("hoist"),

    /**
     * Change of the {@link Role#isMentionable() Role.isMentionable()} value.
     *
     * <p>Expected type: <b>Boolean</b>
     */
    ROLE_MENTIONABLE("mentionable"),


    // EMOTE
    /**
     * Change of the {@link Emote#getName() Emote.getName()} value.
     *
     * <p>Expected type: <b>String</b>
     */
    EMOTE_NAME("name"),

    /**
     * Roles added to {@link Emote#getRoles() Emote.getRoles()} with this action
     * <br>Containing a list of {@link Role Role} IDs
     * <br>Use with {@link Guild#getRoleById(String) Guild.getRoleById(String)}
     *
     * <p>Expected type: <b>List{@literal <String>}</b>
     */
    EMOTE_ROLES_ADD("$add"),

    /**
     * Roles remove from {@link Emote#getRoles() Emote.getRoles()} with this action
     * <br>Containing a list of {@link Role Role} IDs
     * <br>Use with {@link Guild#getRoleById(String) Guild.getRoleById(String)}
     *
     * <p>Expected type: <b>List{@literal <String>}</b>
     */
    EMOTE_ROLES_REMOVE("$remove"),


    // WEBHOOK
    /**
     * Change of the {@link Webhook#getName() Webhook.getName()} value.
     *
     * <p>Expected type: <b>String</b>
     */
    WEBHOOK_NAME("name"),

    /**
     * Change of the {@link Webhook#getDefaultUser() Webhook.getDefaultUser()}'s avatar hash of a Webhook.
     * <br>This is used to build the {@link User#getAvatarUrl() User.getAvatarUrl()}!
     *
     * <p>Expected type: <b>String</b>
     */
    WEBHOOK_ICON("avatar_hash"),

    /**
     * Change of the {@link Webhook#getChannel() Webhook.getChannel()} for
     * the target {@link Webhook Webhook}
     * <br>Use with {@link Guild#getTextChannelById(String) Guild.getTextChannelById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    WEBHOOK_CHANNEL("channel_id"),


    // INVITE
    /**
     * Change of the {@link Invite#getCode() Invite.getCode()} for
     * the target {@link Invite Invite}
     * <br>Use with {@link Invite#resolve(JDA, String)} Invite.resolve(JDA, String)}
     *
     * <p>Expected type: <b>String</b>
     */
    INVITE_CODE("code"),

    /**
     * Change of the {@link Invite#getMaxAge() Invite.getMaxAge()} for
     * the target {@link Invite Invite}
     *
     * <p>Expected type: <b>int</b>
     */
    INVITE_MAX_AGE("max_age"),

    /**
     * Change of the {@link Invite#isTemporary() Invite.isTemporary()} for
     * the target {@link Invite Invite}
     *
     * <p>Expected type: <b>boolean</b>
     */
    INVITE_TEMPORARY("temporary"),

    /**
     * Change of the {@link Invite#getInviter() Invite.getInviter()} ID for
     * the target {@link Invite Invite}
     * <br>Use with {@link JDA#getUserById(String) JDA.getUserById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    INVITE_INVITER("inviter"),

    /**
     * Change of the {@link Invite#getChannel() Invite.getChannel()} ID for
     * the target {@link Invite Invite}
     * <br>Use with {@link JDA#getTextChannelById(String) JDA.getTextChannelById(String)}
     * or {@link JDA#getVoiceChannelById(String) JDA.getVoiceChannelById(String)}
     *
     * <p>Expected type: <b>String</b>
     */
    INVITE_CHANNEL("channel_id"),

    /**
     * Change of the {@link Invite#getUses() Invite.getUses()} for
     * the target {@link Invite Invite}
     *
     * <p>Expected type: <b>int</b>
     */
    INVITE_USES("uses"),

    /**
     * Change of the {@link Invite#getMaxUses() Invite.getMaxUses()} for
     * the target {@link Invite Invite}
     *
     * <p>Expected type: <b>int</b>
     */
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
