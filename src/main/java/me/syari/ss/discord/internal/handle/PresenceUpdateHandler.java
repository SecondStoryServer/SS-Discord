
package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

public class PresenceUpdateHandler extends SocketHandler
{
    private static final Logger log = JDALogger.getLog(PresenceUpdateHandler.class);

    public PresenceUpdateHandler(JDAImpl api)
    {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content)
    {
        return null;
    }
}
