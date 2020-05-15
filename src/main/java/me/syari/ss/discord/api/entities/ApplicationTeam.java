

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface ApplicationTeam extends ISnowflake
{


    @Nullable
    String getIconId();


    @Nonnull
    List<TeamMember> getMembers();


    @Nullable
    default TeamMember getMember(@Nonnull User user)
    {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }


    @Nullable
    default TeamMember getMemberById(@Nonnull String userId)
    {
        return getMemberById(MiscUtil.parseSnowflake(userId));
    }


    @Nullable
    default TeamMember getMemberById(long userId)
    {
        for (TeamMember member : getMembers())
        {
            if (member.getUser().getIdLong() == userId)
                return member;
        }
        return null;
    }
}
