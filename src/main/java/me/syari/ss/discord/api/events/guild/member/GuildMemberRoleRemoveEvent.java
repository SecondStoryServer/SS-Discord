
package me.syari.ss.discord.api.events.guild.member;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class GuildMemberRoleRemoveEvent extends GenericGuildMemberEvent
{
    private final List<Role> removedRoles;

    public GuildMemberRoleRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull List<Role> removedRoles)
    {
        super(api, responseNumber, member);
        this.removedRoles = Collections.unmodifiableList(removedRoles);
    }


    public List<Role> getRoles()
    {
        return removedRoles;
    }
}
