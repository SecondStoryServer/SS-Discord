

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


public interface UnifiedMemberCacheView extends CacheView<Member>
{
    
    @Nonnull
    List<Member> getElementsById(long id);


    @Nonnull
    default List<Member> getElementsById(@Nonnull String id)
    {
        return getElementsById(MiscUtil.parseSnowflake(id));
    }


    @Nonnull
    List<Member> getElementsByUsername(@Nonnull String name, boolean ignoreCase);


    @Nonnull
    default List<Member> getElementsByUsername(@Nonnull String name)
    {
        return getElementsByUsername(name, false);
    }


    @Nonnull
    List<Member> getElementsByNickname(@Nullable String name, boolean ignoreCase);


    @Nonnull
    default List<Member> getElementsByNickname(@Nullable String name)
    {
        return getElementsByNickname(name, false);
    }


    @Nonnull
    List<Member> getElementsWithRoles(@Nonnull Role... roles);


    @Nonnull
    List<Member> getElementsWithRoles(@Nonnull Collection<Role> roles);
}
