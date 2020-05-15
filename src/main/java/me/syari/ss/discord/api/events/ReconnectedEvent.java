
package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates if JDA successfully re-established its connection to the gateway.
 * <br>All Objects have been replaced when this is fired and events were likely missed in the downtime.
 *
 * <p>Can be used to mark the continuation of event flow which was stopped by the {@link DisconnectEvent DisconnectEvent}.
 * User should replace any cached Objects (like User/Guild objects).
 */
public class ReconnectedEvent extends Event
{
    public ReconnectedEvent(@Nonnull JDA api, long responseNumber)
    {
        super(api, responseNumber);
    }
}
