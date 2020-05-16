package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;
import me.syari.ss.discord.annotations.ReplaceWith;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;


public interface Guild extends ISnowflake {
    boolean isLoaded();


    int getMemberCount();


    @Nonnull
    String getName();


    @Nonnull
    Set<String> getFeatures();


    @Nonnull
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getVanityCode()")
    @CheckReturnValue
    RestAction<String> retrieveVanityUrl();


    @Nonnull
    BoostTier getBoostTier();


    default int getMaxBitrate() {
        int maxBitrate = getFeatures().contains("VIP_REGIONS") ? 384000 : 96000;
        return Math.max(maxBitrate, getBoostTier().getMaxBitrate());
    }


    @Nullable
    Member getOwner();


    long getOwnerIdLong();


    boolean isMember(@Nonnull User user);


    @Nonnull
    Member getSelfMember();


    @Nullable
    Member getMember(@Nonnull User user);


    @Nullable
    default Member getMemberById(long userId) {
        return getMemberCache().getElementById(userId);
    }


    @Nonnull
    MemberCacheView getMemberCache();


    @Nullable
    default GuildChannel getGuildChannelById(long id) {
        GuildChannel channel = getTextChannelById(id);
        if (channel == null)
            channel = getStoreChannelById(id);
        if (channel == null)
            channel = getCategoryById(id);
        return channel;
    }


    @Nullable
    default Category getCategoryById(long id) {
        return getCategoryCache().getElementById(id);
    }


    @Nonnull
    SortedSnowflakeCacheView<Category> getCategoryCache();


    @Nullable
    default StoreChannel getStoreChannelById(long id) {
        return getStoreChannelCache().getElementById(id);
    }


    @Nonnull
    SortedSnowflakeCacheView<StoreChannel> getStoreChannelCache();


    @Nullable
    default TextChannel getTextChannelById(long id) {
        return getTextChannelCache().getElementById(id);
    }


    @Nonnull
    default List<TextChannel> getTextChannels() {
        return getTextChannelCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<TextChannel> getTextChannelCache();


    @Nonnull
    default List<GuildChannel> getChannels() {
        return getChannels(true);
    }


    @Nonnull
    List<GuildChannel> getChannels(boolean includeHidden);


    @Nullable
    default Role getRoleById(@Nonnull String id) {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(long id) {
        return getRoleCache().getElementById(id);
    }


    @Nonnull
    default List<Role> getRoles() {
        return getRoleCache().asList();
    }


    @Nonnull
    SortedSnowflakeCacheView<Role> getRoleCache();


    @Nullable
    default Emote getEmoteById(long id) {
        return getEmoteCache().getElementById(id);
    }


    @Nonnull
    SnowflakeCacheView<Emote> getEmoteCache();


    @Nonnull
    Role getPublicRole();


    @Nonnull
    JDA getJDA();


    @Nonnull
    VerificationLevel getVerificationLevel();


    boolean checkVerification();


    @ForRemoval
    @Deprecated
    @DeprecatedSince("4.1.0")
    @ReplaceWith("getJDA().isUnavailable(guild.getIdLong())")
    boolean isAvailable();


    /* From GuildController */


    //////////////////////////


    enum Timeout {
        SECONDS_60(60),
        SECONDS_300(300),
        SECONDS_900(900),
        SECONDS_1800(1800),
        SECONDS_3600(3600);

        private final int seconds;

        Timeout(int seconds) {
            this.seconds = seconds;
        }


        public int getSeconds() {
            return seconds;
        }


        @Nonnull
        public static Timeout fromKey(int seconds) {
            for (Timeout t : values()) {
                if (t.getSeconds() == seconds)
                    return t;
            }
            throw new IllegalArgumentException("Provided key was not recognized. Seconds: " + seconds);
        }
    }


    enum VerificationLevel {
        NONE(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        VERY_HIGH(4),
        UNKNOWN(-1);

        private final int key;

        VerificationLevel(int key) {
            this.key = key;
        }


        public int getKey() {
            return key;
        }


        @Nonnull
        public static VerificationLevel fromKey(int key) {
            for (VerificationLevel level : VerificationLevel.values()) {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum NotificationLevel {
        ALL_MESSAGES(0),
        MENTIONS_ONLY(1),
        UNKNOWN(-1);

        private final int key;

        NotificationLevel(int key) {
            this.key = key;
        }


        public int getKey() {
            return key;
        }


        @Nonnull
        public static NotificationLevel fromKey(int key) {
            for (NotificationLevel level : values()) {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum MFALevel {
        NONE(0),
        TWO_FACTOR_AUTH(1),
        UNKNOWN(-1);

        private final int key;

        MFALevel(int key) {
            this.key = key;
        }


        public int getKey() {
            return key;
        }


        @Nonnull
        public static MFALevel fromKey(int key) {
            for (MFALevel level : values()) {
                if (level.getKey() == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum ExplicitContentLevel {
        OFF(0),
        NO_ROLE(1),
        ALL(2),

        UNKNOWN(-1);

        private final int key;

        ExplicitContentLevel(int key) {
            this.key = key;
        }


        public int getKey() {
            return key;
        }


        @Nonnull
        public static ExplicitContentLevel fromKey(int key) {
            for (ExplicitContentLevel level : values()) {
                if (level.key == key)
                    return level;
            }
            return UNKNOWN;
        }
    }


    enum BoostTier {

        NONE(0, 96000),

        TIER_1(1, 128000),

        TIER_2(2, 256000),

        TIER_3(3, 384000),

        UNKNOWN(-1, Integer.MAX_VALUE);

        private final int key;
        private final int maxBitrate;

        BoostTier(int key, int maxBitrate) {
            this.key = key;
            this.maxBitrate = maxBitrate;
        }


        public int getMaxBitrate() {
            return maxBitrate;
        }


        @Nonnull
        public static BoostTier fromKey(int key) {
            for (BoostTier tier : values()) {
                if (tier.key == key)
                    return tier;
            }
            return UNKNOWN;
        }
    }


}
