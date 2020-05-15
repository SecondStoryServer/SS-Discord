
package me.syari.ss.discord.api.events;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.hooks.EventListener;
import me.syari.ss.discord.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;


public abstract class Event implements GenericEvent
{
    protected final JDA api;
    protected final long responseNumber;

    
    public Event(@Nonnull JDA api, long responseNumber)
    {
        this.api = api;
        this.responseNumber = responseNumber;
    }

    
    public Event(@Nonnull JDA api)
    {
        this(api, api.getResponseTotal());
    }

    @Nonnull
    @Override
    public JDA getJDA()
    {
        return api;
    }

    @Override
    public long getResponseNumber()
    {
        return responseNumber;
    }
}
