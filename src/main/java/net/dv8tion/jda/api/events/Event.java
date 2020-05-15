
package net.dv8tion.jda.api.events;

import net.dv8tion.jda.api.JDA;

import javax.annotation.Nonnull;

/**
 * Top-level event type
 * <br>All events JDA fires are derived from this class.
 *
 * <p>Can be used to check if an Object is a JDA event in {@link net.dv8tion.jda.api.hooks.EventListener EventListener} implementations to distinguish what event is being fired.
 * <br>Adapter implementation: {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter}
 */
public abstract class Event implements GenericEvent
{
    protected final JDA api;
    protected final long responseNumber;

    /**
     * Creates a new Event from the given JDA instance
     *
     * @param api
     *        Current JDA instance
     * @param responseNumber
     *        The sequence number for this event
     *
     * @see   #Event(net.dv8tion.jda.api.JDA)
     */
    public Event(@Nonnull JDA api, long responseNumber)
    {
        this.api = api;
        this.responseNumber = responseNumber;
    }

    /**
     * Creates a new Event from the given JDA instance
     * <br>Uses the current {@link net.dv8tion.jda.api.JDA#getResponseTotal()} as sequence
     *
     * @param api
     *        Current JDA instance
     */
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
