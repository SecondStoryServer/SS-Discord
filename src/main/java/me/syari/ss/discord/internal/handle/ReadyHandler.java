package me.syari.ss.discord.internal.handle;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.syari.ss.discord.api.utils.data.DataArray;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.requests.WebSocketClient;
import org.jetbrains.annotations.NotNull;

public class ReadyHandler extends SocketHandler {

    public ReadyHandler(JDAImpl api) {
        super(api);
    }

    @Override
    protected Long handleInternally(@NotNull DataObject content) {
        System.out.println(">> ReadyHandler");
        DataArray guilds = content.getArray("guilds");
        TLongObjectMap<DataObject> distinctGuilds = new TLongObjectHashMap<>();
        for (int i = 0; i < guilds.length(); i++) {
            DataObject guild = guilds.getObject(i);
            long id = guild.getUnsignedLong("id");
            DataObject previous = distinctGuilds.put(id, guild);
            if (previous != null)
                WebSocketClient.LOG.warn("Found duplicate guild for id {} in ready payload", id);
        }
        return null;
    }
}
