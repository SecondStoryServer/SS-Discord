
package me.syari.ss.discord.api.events;

import com.neovisionaries.ws.client.WebSocketFrame;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.requests.CloseCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;


public class DisconnectEvent extends Event
{
    protected final WebSocketFrame serverCloseFrame;
    protected final WebSocketFrame clientCloseFrame;
    protected final boolean closedByServer;
    protected final OffsetDateTime disconnectTime;

    public DisconnectEvent(
        @Nonnull JDA api,
        @Nullable WebSocketFrame serverCloseFrame, @Nullable WebSocketFrame clientCloseFrame,
        boolean closedByServer, @Nonnull OffsetDateTime disconnectTime)
    {
        super(api);
        this.serverCloseFrame = serverCloseFrame;
        this.clientCloseFrame = clientCloseFrame;
        this.closedByServer = closedByServer;
        this.disconnectTime = disconnectTime;
    }


    @Nullable
    public CloseCode getCloseCode()
    {
        return serverCloseFrame != null ? CloseCode.from(serverCloseFrame.getCloseCode()) : null;
    }


    @Nullable
    public WebSocketFrame getServiceCloseFrame()
    {
        return serverCloseFrame;
    }


    @Nullable
    public WebSocketFrame getClientCloseFrame()
    {
        return clientCloseFrame;
    }


    public boolean isClosedByServer()
    {
        return closedByServer;
    }


    @Nonnull
    public OffsetDateTime getTimeDisconnected()
    {
        return disconnectTime;
    }
}
