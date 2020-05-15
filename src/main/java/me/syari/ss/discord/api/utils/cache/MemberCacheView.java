

package me.syari.ss.discord.api.utils.cache;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


public interface MemberCacheView extends SnowflakeCacheView<Member>
{

    @Nullable
    Member getElementById(long id);


    @Nullable
    default Member getElementById(@Nonnull String id)
    {
        return getElementById(MiscUtil.parseSnowflake(id));
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
