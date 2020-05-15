
package net.dv8tion.jda.api.hooks;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.JDALogger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An {@link net.dv8tion.jda.api.hooks.IEventManager IEventManager} implementation
 * that uses the {@link net.dv8tion.jda.api.hooks.EventListener EventListener} interface for
 * event listeners.
 *
 * <p>This only accepts listeners that implement {@link net.dv8tion.jda.api.hooks.EventListener EventListener}
 * <br>An adapter implementation is {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter} which
 * provides methods for each individual {@link net.dv8tion.jda.api.events.Event}.
 *
 * <p><b>This is the default IEventManager used by JDA</b>
 *
 * @see net.dv8tion.jda.api.hooks.AnnotatedEventManager
 * @see net.dv8tion.jda.api.hooks.IEventManager
 */
public class InterfacedEventManager implements IEventManager
{
    private final CopyOnWriteArrayList<EventListener> listeners = new CopyOnWriteArrayList<>();

    public InterfacedEventManager()
    {

    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException
     *         If the provided listener does not implement {@link net.dv8tion.jda.api.hooks.EventListener EventListener}
     */
    @Override
    public void register(@Nonnull Object listener)
    {
        if (!(listener instanceof EventListener))
        {
            throw new IllegalArgumentException("Listener must implement EventListener");
        }
        listeners.add((EventListener) listener);
    }

    @Override
    public void unregister(@Nonnull Object listener)
    {
        if (!(listener instanceof EventListener))
        {
            //noinspection ConstantConditions
            JDALogger.getLog(getClass()).warn(
                    "Trying to remove a listener that does not implement EventListener: {}",
                    listener == null ? "null" : listener.getClass().getName());
        }

        //noinspection SuspiciousMethodCalls
        listeners.remove(listener);
    }

    @Nonnull
    @Override
    public List<Object> getRegisteredListeners()
    {
        return Collections.unmodifiableList(new ArrayList<>(listeners));
    }

    @Override
    public void handle(@Nonnull GenericEvent event)
    {
        for (EventListener listener : listeners)
        {
            try
            {
                listener.onEvent(event);
            }
            catch (Throwable throwable)
            {
                JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
            }
        }
    }
}
