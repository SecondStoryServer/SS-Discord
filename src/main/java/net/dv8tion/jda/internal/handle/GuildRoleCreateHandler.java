
package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class GuildRoleCreateHandler extends SocketHandler
{

    public GuildRoleCreateHandler(JDAImpl api)
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
            EventCache.LOG.debug("GUILD_ROLE_CREATE was received for a Guild that is not yet cached: {}", content);
            return null;
        }

        Role newRole = getJDA().getEntityBuilder().createRole(guild, content.getObject("role"), guild.getIdLong());
        getJDA().handleEvent(
            new RoleCreateEvent(
                getJDA(), responseNumber,
                newRole));
        return null;
    }
}
