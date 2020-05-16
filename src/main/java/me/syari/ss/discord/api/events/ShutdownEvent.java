package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.CloseCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;


public class ShutdownEvent extends Event {
    protected final OffsetDateTime shutdownTime;
    protected final int code;

    public ShutdownEvent(@Nonnull JDA api, @Nonnull OffsetDateTime shutdownTime, int code) {
        super(api);
        this.shutdownTime = shutdownTime;
        this.code = code;
    }


    @Nonnull
    public OffsetDateTime getTimeShutdown() {
        return shutdownTime;
    }


    @Nullable
    public CloseCode getCloseCode() {
        return CloseCode.from(code);
    }


    public int getCode() {
        return code;
    }
}
