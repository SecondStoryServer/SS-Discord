package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.entities.Member;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface MemberCacheView extends SnowflakeCacheView<Member> {

    @Nullable
    Member getElementById(long id);


    @Nullable
    default Member getElementById(@Nonnull String id) {
        return getElementById(MiscUtil.parseSnowflake(id));
    }


}
