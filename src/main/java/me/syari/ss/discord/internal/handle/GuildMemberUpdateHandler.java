
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;

public class GuildMemberUpdateHandler extends SocketHandler
{

    public GuildMemberUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        return null;
    }
}
