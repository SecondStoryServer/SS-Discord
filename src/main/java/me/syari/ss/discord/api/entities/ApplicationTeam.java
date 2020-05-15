

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface ApplicationTeam extends ISnowflake
{

    String ICON_URL = "https://cdn.discordapp.com/team-icons/%s/%s.png";


    @Nullable
    default TeamMember getOwner()
    {
        return getMemberById(getOwnerIdLong());
    }


    @Nonnull
    default String getOwnerId()
    {
        return Long.toUnsignedString(getOwnerIdLong());
    }


    long getOwnerIdLong();


    @Nullable
    String getIconId();


    @Nullable
    default String getIconUrl()
    {
        String iconId = getIconId();
        return iconId == null ? null : String.format(ICON_URL, getId(), iconId);
    }


    @Nonnull
    List<TeamMember> getMembers();


    default boolean isMember(@Nonnull User user)
    {
        return getMember(user) != null;
    }


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
