
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.events.role.RoleDeleteEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.EmoteImpl;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.MemberImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;

public class GuildRoleDeleteHandler extends SocketHandler
{
    public GuildRoleDeleteHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;

        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("GUILD_ROLE_DELETE was received for a Guild that is not yet cached: {}", content);
            return null;
        }

        final long roleId = content.getLong("role_id");
        Role removedRole = guild.getRolesView().remove(roleId);
        if (removedRole == null)
        {
            //getJDA().getEventCache().cache(EventCache.Type.ROLE, roleId, () -> handle(responseNumber, allContent));
            WebSocketClient.LOG.debug("GUILD_ROLE_DELETE was received for a Role that is not yet cached: {}", content);
            return null;
        }

        //Now that the role is removed from the Guild, remove it from all users and emotes.
        guild.getMembersView().forEach(m ->
        {
            MemberImpl member = (MemberImpl) m;
            member.getRoleSet().remove(removedRole);
        });

        for (Emote emote : guild.getEmoteCache())
        {
            EmoteImpl impl = (EmoteImpl) emote;
            if (impl.canProvideRoles())
                impl.getRoleSet().remove(removedRole);
        }

        getJDA().handleEvent(
            new RoleDeleteEvent(
                getJDA(), responseNumber,
                removedRole));
        getJDA().getEventCache().clear(EventCache.Type.ROLE, roleId);
        return null;
    }
}
