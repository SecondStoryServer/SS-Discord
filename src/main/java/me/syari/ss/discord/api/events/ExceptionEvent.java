

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;

/**
 * Indicates that JDA encountered a Throwable that could not be forwarded to another end-user frontend.
 * <br>For instance this is fired for events in internal WebSocket handling or audio threads.
 * This includes {@link java.lang.Error Errors} and {@link com.neovisionaries.ws.client.WebSocketException WebSocketExceptions}
 *
 * <p>It is not recommended to simply use this and print each event as some throwables were already logged
 * by JDA. See {@link #isLogged()}.
 */
public class ExceptionEvent extends Event
{
    protected final Throwable throwable;
    protected final boolean logged;

    public ExceptionEvent(@Nonnull JDA api, @Nonnull Throwable throwable, boolean logged)
    {
        super(api);
        this.throwable = throwable;
        this.logged = logged;
    }

    /**
     * Whether this Throwable was already printed using the JDA logging system
     *
     * @return True, if this throwable was already logged
     */
    public boolean isLogged()
    {
        return logged;
    }

    /**
     * The cause Throwable for this event
     *
     * @return The cause
     */
    @Nonnull
    public Throwable getCause()
    {
        return throwable;
    }
}
