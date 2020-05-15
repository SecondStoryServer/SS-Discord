
package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that JDA successfully resumed its connection to the gateway.
 * <br>All Objects are still in place and events are replayed.
 *
 * <p>Can be used to marks the continuation of event flow stopped by the {@link DisconnectEvent DisconnectEvent}.
 */
public class ResumedEvent extends Event
{
    public ResumedEvent(@Nonnull JDA api, long responseNumber)
    {
        super(api, responseNumber);
    }
}
