package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import org.jetbrains.annotations.NotNull;

public abstract class SocketHandler {
    protected final JDA api;
    protected long responseNumber;
    protected DataObject allContent;

    public SocketHandler(JDA api) {
        this.api = api;
    }

    public final synchronized void handle(long responseTotal, @NotNull DataObject object) {
        this.allContent = object;
        this.responseNumber = responseTotal;
        final Long guildId = handleInternally(object.getObject("d"));
        if (guildId != null) getJDA().getGuildSetupController().cacheEvent(guildId, object);
        this.allContent = null;
    }

    protected JDA getJDA() {
        return api;
    }

    protected abstract Long handleInternally(DataObject content);
}
