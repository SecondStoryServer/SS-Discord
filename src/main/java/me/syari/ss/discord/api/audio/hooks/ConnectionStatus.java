

package me.syari.ss.discord.api.audio.hooks;

import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.Region;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.managers.AudioManager;


public enum ConnectionStatus
{

    NOT_CONNECTED(false),

    SHUTTING_DOWN(false),

    CONNECTING_AWAITING_ENDPOINT,

    CONNECTING_AWAITING_WEBSOCKET_CONNECT,

    CONNECTING_AWAITING_AUTHENTICATION,

    CONNECTING_ATTEMPTING_UDP_DISCOVERY,

    CONNECTING_AWAITING_READY,

    CONNECTED,

    DISCONNECTED_LOST_PERMISSION(false),

    DISCONNECTED_CHANNEL_DELETED(false),

    DISCONNECTED_REMOVED_FROM_GUILD(false),

    DISCONNECTED_KICKED_FROM_CHANNEL(false),

    DISCONNECTED_REMOVED_DURING_RECONNECT(false),

    DISCONNECTED_AUTHENTICATION_FAILURE,

    AUDIO_REGION_CHANGE,

    //All will attempt to reconnect unless autoReconnect is disabled

    ERROR_LOST_CONNECTION,

    ERROR_CANNOT_RESUME,

    ERROR_WEBSOCKET_UNABLE_TO_CONNECT,

    ERROR_UNSUPPORTED_ENCRYPTION_MODES,

    ERROR_UDP_UNABLE_TO_CONNECT,

    ERROR_CONNECTION_TIMEOUT;

    private final boolean shouldReconnect;

    ConnectionStatus()
    {
        this(true);
    }

    ConnectionStatus(boolean shouldReconnect)
    {
        this.shouldReconnect = shouldReconnect;
    }

    public boolean shouldReconnect()
    {
        return shouldReconnect;
    }
}
