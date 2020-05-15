
package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public abstract class SocketHandler
{
    protected final JDAImpl api;
    protected long responseNumber;
    protected DataObject allContent;

    public SocketHandler(JDAImpl api)
    {
        this.api = api;
    }

    public final synchronized void handle(long responseTotal, DataObject o)
    {
        this.allContent = o;
        this.responseNumber = responseTotal;
        final Long guildId = handleInternally(o.getObject("d"));
        if (guildId != null)
            getJDA().getGuildSetupController().cacheEvent(guildId, o);
        this.allContent = null;
    }

    protected JDAImpl getJDA()
    {
        return api;
    }

    /**
     * Handles a given data-json of the Event handled by this Handler.
     * @param content
     *      the content of the event to handle
     * @return
     *      Guild-id if that guild has a lock, or null if successful
     */
    protected abstract Long handleInternally(DataObject content);

    public static class NOPHandler extends SocketHandler
    {
        public NOPHandler(JDAImpl api)
        {
            super(api);
        }

        @Override
        protected Long handleInternally(DataObject content)
        {
            return null;
        }
    }
}
