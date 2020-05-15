
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.EntityBuilder;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.MemberImpl;

import java.util.LinkedList;
import java.util.List;

public class GuildMemberUpdateHandler extends SocketHandler
{

    public GuildMemberUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long id = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(id))
            return id;

        DataObject userJson = content.getObject("user");
        final long userId = userJson.getLong("id");
        GuildImpl guild = (GuildImpl) getJDA().getGuildById(id);
        if (guild == null)
        {
            //Do not cache this here, it will be outdated once we receive the GUILD_CREATE and could cause invalid cache
            //getJDA().getEventCache().cache(EventCache.Type.GUILD, userId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Got GuildMember update but JDA currently does not have the Guild cached. Ignoring. {}", content);
            return null;
        }

        MemberImpl member = (MemberImpl) guild.getMembersView().get(userId);
        if (member == null)
        {
            EntityBuilder.LOG.debug("Creating member from GUILD_MEMBER_UPDATE {}", content);
            member = getJDA().getEntityBuilder().createMember(guild, content);
        }

        List<Role> newRoles = toRolesList(guild, content.getArray("roles"));
        getJDA().getEntityBuilder().updateMember(guild, member, content, newRoles);
        return null;
    }

    private List<Role> toRolesList(GuildImpl guild, DataArray array)
    {
        LinkedList<Role> roles = new LinkedList<>();
        for(int i = 0; i < array.length(); i++)
        {
            final long id = array.getLong(i);
            Role r = guild.getRolesView().get(id);
            if (r != null)
            {
                roles.add(r);
            }
            else
            {
                getJDA().getEventCache().cache(EventCache.Type.ROLE, id, responseNumber, allContent, this::handle);
                EventCache.LOG.debug("Got GuildMember update but one of the Roles for the Member is not yet cached.");
                return null;
            }
        }
        return roles;
    }
}
