package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.utils.JDALogger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class InterfacedEventManager implements IEventManager {
    private final CopyOnWriteArrayList<ListenerAdapter> listeners = new CopyOnWriteArrayList<>();

    public InterfacedEventManager() {

    }


    @Override
    public void register(@Nonnull Object listener) {
        if (!(listener instanceof ListenerAdapter)) {
            throw new IllegalArgumentException("Listener must implement EventListener");
        }
        listeners.add((ListenerAdapter) listener);
    }

    @Override
    public void unregister(@Nonnull Object listener) {
        if (!(listener instanceof ListenerAdapter)) {
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
    public List<Object> getRegisteredListeners() {
        return Collections.unmodifiableList(new ArrayList<>(listeners));
    }

    @Override
    public void handle(@Nonnull MessageReceivedEvent event) {
        for (ListenerAdapter listener : listeners) {
            try {
                listener.onMessageReceived(event);
            } catch (Throwable throwable) {
                JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
            }
        }
    }
}
