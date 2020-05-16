package me.syari.ss.discord.internal.utils.cache;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.cache.MemberCacheView;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MemberCacheViewImpl extends SnowflakeCacheViewImpl<Member> implements MemberCacheView {
    public MemberCacheViewImpl() {
        super(Member.class, Member::getEffectiveName);
    }

    @Override
    public Member getElementById(long id) {
        return get(id);
    }

    @Nonnull
    @Override
    public List<Member> getElementsByUsername(@Nonnull String name, boolean ignoreCase) {
        Checks.notEmpty(name, "Name");
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            final String nick = member.getUser().getName();
            if (equals(ignoreCase, nick, name))
                members.add(member);
        });
        return Collections.unmodifiableList(members);
    }

    @Nonnull
    @Override
    public List<Member> getElementsByNickname(@Nullable String name, boolean ignoreCase) {
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            final String nick = member.getNickname();
            if (nick == null) {
                if (name == null)
                    members.add(member);
                return;
            }

            if (equals(ignoreCase, nick, name))
                members.add(member);
        });
        return Collections.unmodifiableList(members);
    }

    @Nonnull
    @Override
    public List<Member> getElementsWithRoles(@Nonnull Role... roles) {
        Checks.notNull(roles, "Roles");
        return getElementsWithRoles(Arrays.asList(roles));
    }

    @Nonnull
    @Override
    public List<Member> getElementsWithRoles(@Nonnull Collection<Role> roles) {
        Checks.noneNull(roles, "Roles");
        if (isEmpty())
            return Collections.emptyList();
        List<Member> members = new ArrayList<>();
        forEach(member ->
        {
            if (member.getRoles().containsAll(roles))
                members.add(member);
        });
        return members;
    }
}
