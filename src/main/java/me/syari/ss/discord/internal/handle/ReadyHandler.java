package me.syari.ss.discord.internal.handle;

import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDA;
import org.jetbrains.annotations.NotNull;

public class ReadyHandler extends SocketHandler {
    public ReadyHandler(JDA api) {
        super(api);
    }

    @Override
    protected Long handleInternally(@NotNull DataObject content) {
        System.out.println(">> ReadyHandler");
        return null;
    }
}
