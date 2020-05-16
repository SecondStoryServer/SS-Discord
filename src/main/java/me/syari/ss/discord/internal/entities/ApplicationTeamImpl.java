

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.ApplicationTeam;
import me.syari.ss.discord.api.entities.TeamMember;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ApplicationTeamImpl implements ApplicationTeam
{
    private final List<TeamMember> members;
    private final long id;

    public ApplicationTeamImpl(List<TeamMember> members, long id)
    {
        this.members = Collections.unmodifiableList(members);
        this.id = id;
    }

    @Nonnull
    @Override
    public List<TeamMember> getMembers()
    {
        return members;
    }

    @Override
    public long getIdLong()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof ApplicationTeamImpl))
            return false;
        ApplicationTeamImpl app = (ApplicationTeamImpl) obj;
        return app.id == this.id;
    }

    @Override
    public String toString()
    {
        return "ApplicationTeam(" + getId() + ')';
    }
}
