package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import org.jetbrains.annotations.NotNull;

public abstract class SocketHandler {
    protected final JDAImpl api;
    protected long responseNumber;
    protected DataObject allContent;

    public SocketHandler(JDAImpl api) {
        this.api = api;
    }

    public final synchronized void handle(long responseTotal, @NotNull DataObject object) {
        this.allContent = object;
        this.responseNumber = responseTotal;
        final Long guildId = handleInternally(object.getObject("d"));
        if (guildId != null)
            getJDA().getGuildSetupController().cacheEvent(guildId, object);
        this.allContent = null;
    }

    protected JDAImpl getJDA() {
        return api;
    }


    protected abstract Long handleInternally(DataObject content);

    public static class NOPHandler extends SocketHandler {
        public NOPHandler(JDAImpl api) {
            super(api);
        }

        @Override
        protected Long handleInternally(DataObject content) {
            return null;
        }
    }
}
