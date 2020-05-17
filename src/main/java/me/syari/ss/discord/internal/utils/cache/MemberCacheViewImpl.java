package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.internal.entities.Member;

public class MemberCacheViewImpl extends SnowflakeCacheViewImpl<Member> implements MemberCacheView {
    public MemberCacheViewImpl() {
        super(Member.class);
    }

    @Override
    public Member getElementById(long id) {
        return get(id);
    }

}
