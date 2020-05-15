

package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


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


    public boolean isLogged()
    {
        return logged;
    }


    @Nonnull
    public Throwable getCause()
    {
        return throwable;
    }
}
