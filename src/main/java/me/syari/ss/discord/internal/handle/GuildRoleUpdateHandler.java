
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.entities.Role;
import me.syari.ss.discord.api.events.role.update.*;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.entities.RoleImpl;

import java.util.Objects;

public class GuildRoleUpdateHandler extends SocketHandler
{
    public GuildRoleUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(guildId))
            return guildId;

        DataObject rolejson = content.getObject("role");
        GuildImpl guild = (GuildImpl) getJDA().getGuildById(guildId);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received a Role Update for a Guild that is not yet cached: {}", content);
            return null;
        }

        final long roleId = rolejson.getLong("id");
        RoleImpl role = (RoleImpl) guild.getRolesView().get(roleId);
        if (role == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.ROLE, roleId, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received a Role Update for Role that is not yet cached: {}", content);
            return null;
        }

        String name = rolejson.getString("name");
        int color = rolejson.getInt("color");
        if (color == 0)
            color = Role.DEFAULT_COLOR_RAW;
        int position = rolejson.getInt("position");
        long permissions = rolejson.getLong("permissions");
        boolean hoisted = rolejson.getBoolean("hoist");
        boolean mentionable = rolejson.getBoolean("mentionable");

        if (!Objects.equals(name, role.getName()))
        {
            String oldName = role.getName();
            role.setName(name);
            getJDA().handleEvent(
                    new RoleUpdateNameEvent(
                            getJDA(), responseNumber,
                            role, oldName));
        }
        if (color != role.getColorRaw())
        {
            int oldColor = role.getColorRaw();
            role.setColor(color);
            getJDA().handleEvent(
                    new RoleUpdateColorEvent(
                            getJDA(), responseNumber,
                            role, oldColor));
        }
        if (!Objects.equals(position, role.getPositionRaw()))
        {
            int oldPosition = role.getPosition();
            int oldPositionRaw = role.getPositionRaw();
            role.setRawPosition(position);
            getJDA().handleEvent(
                    new RoleUpdatePositionEvent(
                            getJDA(), responseNumber,
                            role, oldPosition, oldPositionRaw));
        }
        if (!Objects.equals(permissions, role.getPermissionsRaw()))
        {
            long oldPermissionsRaw = role.getPermissionsRaw();
            role.setRawPermissions(permissions);
            getJDA().handleEvent(
                    new RoleUpdatePermissionsEvent(
                            getJDA(), responseNumber,
                            role, oldPermissionsRaw));
        }

        if (hoisted != role.isHoisted())
        {
            boolean wasHoisted = role.isHoisted();
            role.setHoisted(hoisted);
            getJDA().handleEvent(
                    new RoleUpdateHoistedEvent(
                            getJDA(), responseNumber,
                            role, wasHoisted));
        }
        if (mentionable != role.isMentionable())
        {
            boolean wasMentionable = role.isMentionable();
            role.setMentionable(mentionable);
            getJDA().handleEvent(
                    new RoleUpdateMentionableEvent(
                            getJDA(), responseNumber,
                            role, wasMentionable));
        }
        return null;
    }
}
