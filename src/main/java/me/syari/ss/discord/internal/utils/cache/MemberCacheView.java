package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.internal.utils.MiscUtil;
import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MemberCacheView extends SnowflakeCacheView<Member> implements ISnowflakeCacheView<Member> {
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
