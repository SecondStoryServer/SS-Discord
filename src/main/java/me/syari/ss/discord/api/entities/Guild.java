
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.exceptions.HierarchyException;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.managers.GuildManager;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.ChannelAction;
import me.syari.ss.discord.api.requests.restaction.MemberAction;
import me.syari.ss.discord.api.requests.restaction.RoleAction;
import me.syari.ss.discord.api.requests.restaction.order.CategoryOrderAction;
import me.syari.ss.discord.api.requests.restaction.order.ChannelOrderAction;
import me.syari.ss.discord.api.requests.restaction.order.RoleOrderAction;
import me.syari.ss.discord.api.requests.restaction.pagination.AuditLogPaginationAction;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.internal.requests.DeferredRestAction;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.requests.restaction.AuditableRestActionImpl;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public interface Guild extends ISnowflake
{

    String ICON_URL = "https://cdn.discordapp.com/icons/%s/%s.%s";

    String SPLASH_URL = "https://cdn.discordapp.com/splashes/%s/%s.png";

    String BANNER_URL = "https://cdn.discordapp.com/banners/%s/%s.png";


    boolean isLoaded();


    int getMemberCount();


    @Nonnull
    String getName();


    @Nullable
    String getIconId();


    @Nonnull
    Set<String> getFeatures();


    @Nullable
    String getSplashId();


    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getVanityCode()")
    @CheckReturnValue
    RestAction<String> retrieveVanityUrl();


    @Nullable
    String getVanityCode();


    @Nullable
    String getDescription();


    @Nullable
    String getBannerId();


    @Nonnull
    BoostTier getBoostTier();


    int getBoostCount();


    default int getMaxBitrate()
    {
        int maxBitrate = getFeatures().contains("VIP_REGIONS") ? 384000 : 96000;
        return Math.max(maxBitrate, getBoostTier().getMaxBitrate());
    }


    int getMaxMembers();


    int getMaxPresences();


    @Nullable
    VoiceChannel getAfkChannel();


    @Nullable
    TextChannel getSystemChannel();


    @Nullable
    Member getOwner();


    long getOwnerIdLong();


    @Nonnull
    default String getOwnerId()
    {
        return Long.toUnsignedString(getOwnerIdLong());
    }


    @Nonnull
    Timeout getAfkTimeout();


    @Nonnull
    default Region getRegion()
    {
        return Region.fromKey(getRegionRaw());
    }


    @Nonnull
    String getRegionRaw();


    boolean isMember(@Nonnull User user);


    @Nonnull
    Member getSelfMember();


    @Nullable
    Member getMember(@Nonnull User user);


    @Nullable
    default Member getMemberById(@Nonnull String userId)
    {
        return getMemberCache().getElementById(userId);
    }


    @Nullable
    default Member getMemberById(long userId)
    {
        return getMemberCache().getElementById(userId);
    }


    @Nonnull
    MemberCacheView getMemberCache();


    @Nullable
    default GuildChannel getGuildChannelById(long id)
    {
        GuildChannel channel = getTextChannelById(id);
        if (channel == null)
            channel = getVoiceChannelById(id);
        if (channel == null)
            channel = getStoreChannelById(id);
        if (channel == null)
            channel = getCategoryById(id);
        return channel;
    }


    @Nullable
    default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id)
    {
        Checks.notNull(type, "ChannelType");
        switch (type)
        {
            case TEXT:
                return getTextChannelById(id);
            case VOICE:
                return getVoiceChannelById(id);
            case STORE:
                return getStoreChannelById(id);
            case CATEGORY:
                return getCategoryById(id);
        }
        return null;
    }


    @Nullable
    default Category getCategoryById(long id)
    {
        return getCategoryCache().getElementById(id);
    }


    @Nonnull
    default List<Category> getCategories()
    {
        return getCategoryCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<Category> getCategoryCache();


    @Nullable
    default StoreChannel getStoreChannelById(long id)
    {
        return getStoreChannelCache().getElementById(id);
    }


    @Nonnull
    default List<StoreChannel> getStoreChannels()
    {
        return getStoreChannelCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache();


    @Nullable
    default TextChannel getTextChannelById(long id)
    {
        return getTextChannelCache().getElementById(id);
    }


    @Nonnull
    default List<TextChannel> getTextChannels()
    {
        return getTextChannelCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<TextChannel> getTextChannelCache();


    @Nullable
    default VoiceChannel getVoiceChannelById(long id)
    {
        return getVoiceChannelCache().getElementById(id);
    }


    @Nonnull
    default List<VoiceChannel> getVoiceChannels()
    {
        return getVoiceChannelCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<VoiceChannel> getVoiceChannelCache();


    @Nonnull
    default List<GuildChannel> getChannels()
    {
        return getChannels(true);
    }


    @Nonnull
    List<GuildChannel> getChannels(boolean includeHidden);


    @Nullable
    default Role getRoleById(@Nonnull String id)
    {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(long id)
    {
        return getRoleCache().getElementById(id);
    }


    @Nonnull
    default List<Role> getRoles()
    {
        return getRoleCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<Role> getRoleCache();


    @Nullable
    default Emote getEmoteById(@Nonnull String id)
    {
        return getEmoteCache().getElementById(id);
    }


    @Nullable
    default Emote getEmoteById(long id)
    {
        return getEmoteCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();


    @Nonnull
    @CheckReturnValue
    RestAction<ListedEmote> retrieveEmoteById(@Nonnull String id);


    @Nonnull
    @CheckReturnValue
    RestAction<Ban> retrieveBanById(@Nonnull String userId);


    @Nonnull
    Role getPublicRole();


    @Nonnull
    @CheckReturnValue
    RestAction<Void> delete(@Nullable String mfaCode);


    @Nonnull
    JDA getJDA();


    @Nonnull
    VerificationLevel getVerificationLevel();


    @Nonnull
    NotificationLevel getDefaultNotificationLevel();


    @Nonnull
    MFALevel getRequiredMFALevel();


    @Nonnull
    ExplicitContentLevel getExplicitContentLevel();


    boolean checkVerification();


    @ForRemoval
    @Deprecated
    @DeprecatedSince("4.1.0")
    @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
    boolean isAvailable();


    @Nonnull
    default RestAction<Member> retrieveMemberById(@Nonnull String id)
    {
        return retrieveMemberById(MiscUtil.parseSnowflake(id));
    }


    @Nonnull
    RestAction<Member> retrieveMemberById(long id);


    /* From GuildController */


    @Nonnull
    @CheckReturnValue
    RestAction<Void> moveVoiceMember(@Nonnull Member member, @Nullable VoiceChannel voiceChannel);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> modifyNickname(@Nonnull Member member, @Nullable String nickname);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> kick(@Nonnull Member member, @Nullable String reason);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> kick(@Nonnull String userId, @Nullable String reason);


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> kick(@Nonnull Member member)
    {
        return kick(member, null);
    }


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> ban(@Nonnull User user, int delDays, @Nullable String reason);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> ban(@Nonnull String userId, int delDays, @Nullable String reason);


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull Member member, int delDays, @Nullable String reason)
    {
        Checks.notNull(member, "Member");
        //Don't check if the provided member is from this guild. It doesn't matter if they are or aren't.

        return ban(member.getUser(), delDays, reason);
    }


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> ban(@Nonnull Member member, int delDays)
    {
        return ban(member, delDays, null);
    }


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> unban(@Nonnull String userId);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> deafen(@Nonnull Member member, boolean deafen);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> mute(@Nonnull Member member, boolean mute);


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> addRoleToMember(@Nonnull Member member, @Nonnull Role role);


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> addRoleToMember(long userId, @Nonnull Role role)
    {
        Checks.notNull(role, "Role");
        Checks.check(role.getGuild().equals(this), "Role must be from the same guild! Trying to use role from %s in %s", role.getGuild().toString(), toString());

        Member member = getMemberById(userId);
        if (member != null)
            return addRoleToMember(member, role);
        if (!getSelfMember().hasPermission(Permission.MANAGE_ROLES))
            throw new InsufficientPermissionException(this, Permission.MANAGE_ROLES);
        if (!getSelfMember().canInteract(role))
            throw new HierarchyException("Can't modify a role with higher or equal highest role than yourself! Role: " + role.toString());
        Route.CompiledRoute route = Route.Guilds.ADD_MEMBER_ROLE.compile(getId(), Long.toUnsignedString(userId), role.getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> removeRoleFromMember(@Nonnull Member member, @Nonnull Role role);


    @Nonnull
    @CheckReturnValue
    default AuditableRestAction<Void> removeRoleFromMember(long userId, @Nonnull Role role)
    {
        Checks.notNull(role, "Role");
        Checks.check(role.getGuild().equals(this), "Role must be from the same guild! Trying to use role from %s in %s", role.getGuild().toString(), toString());

        Member member = getMemberById(userId);
        if (member != null)
            return removeRoleFromMember(member, role);
        if (!getSelfMember().hasPermission(Permission.MANAGE_ROLES))
            throw new InsufficientPermissionException(this, Permission.MANAGE_ROLES);
        if (!getSelfMember().canInteract(role))
            throw new HierarchyException("Can't modify a role with higher or equal highest role than yourself! Role: " + role.toString());
        Route.CompiledRoute route = Route.Guilds.REMOVE_MEMBER_ROLE.compile(getId(), Long.toUnsignedString(userId), role.getId());
        return new AuditableRestActionImpl<>(getJDA(), route);
    }


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> modifyMemberRoles(@Nonnull Member member, @Nonnull Collection<Role> roles);


    @Nonnull
    @CheckReturnValue
    ChannelAction<TextChannel> createTextChannel(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    ChannelAction<Category> createCategory(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    RoleAction createRole();


    @Nonnull
    @CheckReturnValue
    RoleOrderAction modifyRolePositions(boolean useAscendingOrder);

    //////////////////////////


    enum Timeout
    {
        SECONDS_60(60),
        SECONDS_300(300),
        SECONDS_900(900),
        SECONDS_1800(1800),
        SECONDS_3600(3600);

        private final int seconds;

        Timeout(int seconds)
        {
            this.seconds = seconds;
        }


        public int getSeconds()
        {
            return seconds;
        }


        @Nonnull
        public static Timeout fromKey(int seconds)
        {
            for (Timeout t : values())
            {
                if (t.getSeconds() == seconds)
                    return t;
            }
            throw new IllegalArgumentException("Provided key was not recognized. Seconds: " + seconds);
        }
    }


    enum VerificationLevel
    {
        NONE(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        VERY_HIGH(4),
        UNKNOWN(-1);

        private final int key;

        VerificationLevel(int key)
        {
            this.key = key;
        }


        public int getKey()
        {
            return key;
        }


        @Nonnull
        public static VerificationLevel fromKey(int key)
        {
            for (VerificationLevel level : VerificationLevel.values())
            {
                if(level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum NotificationLevel
    {
        ALL_MESSAGES(0),
        MENTIONS_ONLY(1),
        UNKNOWN(-1);

        private final int key;

        NotificationLevel(int key)
        {
            this.key = key;
        }


        public int getKey()
        {
            return key;
        }


        @Nonnull
        public static NotificationLevel fromKey(int key)
        {
            for (NotificationLevel level : values())
            {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum MFALevel
    {
        NONE(0),
        TWO_FACTOR_AUTH(1),
        UNKNOWN(-1);

        private final int key;

        MFALevel(int key)
        {
            this.key = key;
        }


        public int getKey()
        {
            return key;
        }


        @Nonnull
        public static MFALevel fromKey(int key)
        {
            for (MFALevel level : values())
            {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum ExplicitContentLevel
    {
        OFF(0, "Don't scan any messages."),
        NO_ROLE(1, "Scan messages from members without a role."),
        ALL(2, "Scan messages sent by all members."),

        UNKNOWN(-1, "Unknown filter level!");

        private final int key;
        private final String description;

        ExplicitContentLevel(int key, String description)
        {
            this.key = key;
            this.description = description;
        }


        public int getKey()
        {
            return key;
        }


        @Nonnull
        public static ExplicitContentLevel fromKey(int key)
        {
            for (ExplicitContentLevel level : values())
            {
                if (level.key == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum BoostTier
    {

        NONE(0, 96000, 50),

        TIER_1(1, 128000, 100),

        TIER_2(2, 256000, 150),

        TIER_3(3, 384000, 250),

        UNKNOWN(-1, Integer.MAX_VALUE, Integer.MAX_VALUE);

        private final int key;
        private final int maxBitrate;
        private final int maxEmotes;

        BoostTier(int key, int maxBitrate, int maxEmotes)
        {
            this.key = key;
            this.maxBitrate = maxBitrate;
            this.maxEmotes = maxEmotes;
        }


        public int getMaxBitrate()
        {
            return maxBitrate;
        }


        public int getMaxEmotes() 
        {
            return maxEmotes;
        }


        @Nonnull
        public static BoostTier fromKey(int key)
        {
            for (BoostTier tier : values())
            {
                if (tier.key == key)
                    return tier;
            }
            return UNKNOWN;
        }
    }


    class Ban
    {
        protected final User user;
        protected final String reason;

        public Ban(User user, String reason)
        {
            this.user = user;
            this.reason = reason;
        }


        @Override
        public String toString()
        {
            return "GuildBan:" + user + (reason == null ? "" : '(' + reason + ')');
        }
    }
}
