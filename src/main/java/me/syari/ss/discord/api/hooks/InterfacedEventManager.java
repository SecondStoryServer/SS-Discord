package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;
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
