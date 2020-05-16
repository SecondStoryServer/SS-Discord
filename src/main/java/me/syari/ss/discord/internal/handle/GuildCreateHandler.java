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
            // This can happen in 3 scenarios:
            //
            //   1) The guild is provided in guild streaming during initial session setup
            //   2) The guild has just been joined by the bot (added through moderator)
            //   3) The guild was marked unavailable and has come back
            //
            // The controller will fire an appropriate event for each case.
            getJDA().getGuildSetupController().onCreate(id, content);
        }

        // Anything else is either a duplicate event or unexpected behavior
        return null;
    }
}
