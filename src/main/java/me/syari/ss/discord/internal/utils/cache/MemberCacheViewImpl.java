package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;

public class MemberCacheViewImpl extends SnowflakeCacheViewImpl<Member> implements MemberCacheView {
    public MemberCacheViewImpl() {
        super(Member.class);
    }

    @Override
    public Member getElementById(long id) {
        return get(id);
    }

}
