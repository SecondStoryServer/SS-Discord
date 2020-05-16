package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.entities.GuildImpl;

public class GuildCreateHandler extends SocketHandler {

    public GuildCreateHandler(JDAImpl api) {
        super(api);
    }

    @Override
    protected Long handleInternally(DataObject content) {
        System.out.println(">> GuildCreateHandler");
        final long id = content.getLong("id");
        GuildImpl guild = (GuildImpl) getJDA().getGuildById(id);
        if (guild == null) {
            getJDA().getGuildSetupController().onCreate(id, content);
        }

        return null;
    }
}
