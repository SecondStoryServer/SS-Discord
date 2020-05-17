package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.SnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MemberCacheView extends SnowflakeCacheViewImpl<Member> implements SnowflakeCacheView<Member> {
    public MemberCacheView() {
        super(Member.class);
    }

    @Override
    public Member getElementById(long id) {
        return get(id);
    }


    @Nullable
    public Member getElementById(@NotNull String id) {
        return getElementById(MiscUtil.parseSnowflake(id));
    }
}
