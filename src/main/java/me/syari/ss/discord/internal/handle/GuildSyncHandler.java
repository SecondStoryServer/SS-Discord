

package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.api.utils.data.DataObject;

public class GuildSyncHandler extends SocketHandler
{
    public GuildSyncHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        final long guildId = content.getLong("id");
        getJDA().getGuildSetupController().onSync(guildId, content);
        return null;
    }
}
