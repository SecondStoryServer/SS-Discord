package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.utils.cache.ISnowflakeCacheView;
import me.syari.ss.discord.internal.entities.Member;

public class MemberCacheView extends SnowflakeCacheView<Member> implements ISnowflakeCacheView<Member> {
    public MemberCacheView() {
        super(Member.class);
    }

    @Override
    public Member getElementById(long id) {
        return get(id);
    }

}
