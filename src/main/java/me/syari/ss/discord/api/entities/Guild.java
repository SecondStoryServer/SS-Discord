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


    @Nullable
    Member getOwner();


    long getOwnerIdLong();


    boolean isMember(@Nonnull User user);


    @Nullable
    Member getMember(@Nonnull User user);


    @Nullable
    default Member getMemberById(long userId) {
        return getMemberCache().getElementById(userId);
    }


    @Nonnull
    MemberCacheView getMemberCache();


    @Nullable
    default Role getRoleById(@Nonnull String id) {
        return getRoleCache().getElementById(id);
    }


    @Nullable
    default Role getRoleById(long id) {
        return getRoleCache().getElementById(id);
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


    enum VerificationLevel {
        NONE(),
        LOW(),
        MEDIUM(),
        HIGH(),
        VERY_HIGH(),
        UNKNOWN();

        VerificationLevel() {
        }
    }


    enum BoostTier {

        NONE(0),

        TIER_1(1),

        TIER_2(2),

        TIER_3(3),

        UNKNOWN(-1);

        private final int key;

        BoostTier(int key) {
            this.key = key;
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
