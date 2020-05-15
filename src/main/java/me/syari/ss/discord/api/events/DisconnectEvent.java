
package me.syari.ss.discord.api.events;

import com.neovisionaries.ws.client.WebSocketFrame;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.requests.CloseCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;

/**
 * Indicates that JDA has been disconnected from the remote server.
 * <br>When this event is fired JDA will try to reconnect if possible
 * unless {@link JDABuilder#setAutoReconnect(boolean) JDABuilder.setAutoReconnect(Boolean)}
 * has been provided {@code false} or the disconnect was too fatal in which case a {@link ShutdownEvent} is fired.
 *
 * <p>When reconnecting was successful either a {@link ReconnectedEvent ReconnectEvent}
 * <b>or</b> {@link ResumedEvent ResumedEvent} is fired.
 */
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

    /**
     * Possibly-null {@link CloseCode CloseCode}
     * representing the meaning for this DisconnectEvent
     *
     * <p><b>This is {@code null} if this disconnect did either not happen because the Service closed the session
     * (see {@link #isClosedByServer()}) or if there is no mapped CloseCode enum constant for the service close code!</b>
     *
     * @return Possibly-null {@link CloseCode CloseCode}
     */
    @Nullable
    public CloseCode getCloseCode()
    {
        return serverCloseFrame != null ? CloseCode.from(serverCloseFrame.getCloseCode()) : null;
    }

    /**
     * The close frame discord sent to us
     *
     * @return The {@link com.neovisionaries.ws.client.WebSocketFrame WebSocketFrame} discord sent as closing handshake
     */
    @Nullable
    public WebSocketFrame getServiceCloseFrame()
    {
        return serverCloseFrame;
    }

    /**
     * The close frame we sent to discord
     *
     * @return The {@link com.neovisionaries.ws.client.WebSocketFrame WebSocketFrame} we sent as closing handshake
     */
    @Nullable
    public WebSocketFrame getClientCloseFrame()
    {
        return clientCloseFrame;
    }

    /**
     * Whether the connection was closed by discord
     *
     * @return True, if discord closed our connection
     */
    public boolean isClosedByServer()
    {
        return closedByServer;
    }

    /**
     * Time at which we noticed the disconnection
     *
     * @return Time of closure
     */
    @Nonnull
    public OffsetDateTime getTimeDisconnected()
    {
        return disconnectTime;
    }
}
