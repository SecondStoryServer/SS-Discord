

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Meta-data for the team of an application.
 *
 * @see ApplicationInfo#getTeam()
 */
public interface ApplicationTeam extends ISnowflake
{
    /** Template for {@link #getIconUrl()} */
    String ICON_URL = "https://cdn.discordapp.com/team-icons/%s/%s.png";

    /**
     * Searches for the {@link TeamMember TeamMember}
     * in {@link #getMembers()} that has the same user id as {@link #getOwnerIdLong()}.
     * <br>Its possible although unlikely that the owner of the team is not a member, in that case this will be null.
     *
     * @return Possibly-null {@link TeamMember TeamMember} who owns the team
     */
    @Nullable
    default TeamMember getOwner()
    {
        return getMemberById(getOwnerIdLong());
    }

    /**
     * The id for the user who owns this team.
     *
     * @return The owner id
     */
    @Nonnull
    default String getOwnerId()
    {
        return Long.toUnsignedString(getOwnerIdLong());
    }

    /**
     * The id for the user who owns this team.
     *
     * @return The owner id
     */
    long getOwnerIdLong();

    /**
     * The id hash for the icon of this team.
     *
     * @return The icon id, or null if no icon is applied
     *
     * @see    #getIconUrl()
     */
    @Nullable
    String getIconId();

    /**
     * The url for the icon of this team.
     *
     * @return The icon url, or null if no icon is applied
     */
    @Nullable
    default String getIconUrl()
    {
        String iconId = getIconId();
        return iconId == null ? null : String.format(ICON_URL, getId(), iconId);
    }

    /**
     * The {@link TeamMember Team Members}.
     *
     * @return Immutable list of team members
     */
    @Nonnull
    List<TeamMember> getMembers();

    /**
     * Check whether {@link #getMember(User)} returns null for the provided user.
     *
     * @param  user
     *         The user to check
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return True, if the provided user is a member of this team
     */
    default boolean isMember(@Nonnull User user)
    {
        return getMember(user) != null;
    }

    /**
     * Retrieves the {@link TeamMember TeamMember} instance
     * for the provided user. If the user is not a member of this team, null is returned.
     *
     * @param  user
     *         The user for the team member
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The {@link TeamMember TeamMember} for the user or null
     */
    @Nullable
    default TeamMember getMember(@Nonnull User user)
    {
        Checks.notNull(user, "User");
        return getMemberById(user.getIdLong());
    }

    /**
     * Retrieves the {@link TeamMember TeamMember} instance
     * for the provided user id. If the user is not a member of this team, null is returned.
     *
     * @param  userId
     *         The user id for the team member
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The {@link TeamMember TeamMember} for the user or null
     */
    @Nullable
    default TeamMember getMemberById(@Nonnull String userId)
    {
        return getMemberById(MiscUtil.parseSnowflake(userId));
    }

    /**
     * Retrieves the {@link TeamMember TeamMember} instance
     * for the provided user id. If the user is not a member of this team, null is returned.
     *
     * @param  userId
     *         The user id for the team member
     *
     * @return The {@link TeamMember TeamMember} for the user or null
     */
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
