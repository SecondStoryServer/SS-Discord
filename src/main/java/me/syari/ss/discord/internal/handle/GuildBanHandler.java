
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.events.guild.GuildBanEvent;
import me.syari.ss.discord.api.events.guild.GuildUnbanEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.utils.JDALogger;

public class GuildBanHandler extends SocketHandler
{
    private final boolean banned;

    public GuildBanHandler(JDAImpl api, boolean banned)
    {
        super(api);
        this.banned = banned;
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long id = content.getLong("guild_id");
        if (getJDA().getGuildSetupController().isLocked(id))
            return id;

        DataObject userJson = content.getObject("user");
        GuildImpl guild = (GuildImpl) getJDA().getGuildById(id);
        if (guild == null)
        {
            getJDA().getEventCache().cache(EventCache.Type.GUILD, id, responseNumber, allContent, this::handle);
            EventCache.LOG.debug("Received Guild Member {} event for a Guild not yet cached.", JDALogger.getLazyString(() -> banned ? "Ban" : "Unban"));
            return null;
        }

        User user = getJDA().getEntityBuilder().createFakeUser(userJson, false);

        if (banned)
        {
            getJDA().handleEvent(
                    new GuildBanEvent(
                            getJDA(), responseNumber,
                            guild, user));
        }
        else
        {
            getJDA().handleEvent(
                    new GuildUnbanEvent(
                            getJDA(), responseNumber,
                            guild, user));
        }
        return null;
    }
}
