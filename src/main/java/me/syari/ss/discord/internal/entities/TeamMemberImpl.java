package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.TeamMember;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TeamMemberImpl implements TeamMember {
    private final User user;
    private final MembershipState state;
    private final long teamId;

    public TeamMemberImpl(User user, MembershipState state, long teamId) {
        this.user = user;
        this.state = state;
        this.teamId = teamId;
    }

    @Nonnull
    @Override
    public User getUser() {
        return user;
    }

    @Nonnull
    @Override
    public MembershipState getMembershipState() {
        return state;
    }

    @Override
    public long getTeamIdLong() {
        return teamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, teamId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof TeamMemberImpl))
            return false;
        TeamMemberImpl member = (TeamMemberImpl) obj;
        return member.teamId == this.teamId && member.user.equals(this.user);
    }

    @Override
    public String toString() {
        return "TeamMember(" + getTeamId() + ", " + user + ")";
    }
}
