package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.api.utils.cache.SortedSnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Emote;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface Guild extends ISnowflake {


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
}
