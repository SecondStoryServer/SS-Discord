package me.syari.ss.discord.internal.hooks;

import me.syari.ss.discord.api.events.MessageReceivedEvent;
import me.syari.ss.discord.api.hooks.EventManager;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;

public class EventManagerProxy implements EventManager {
    private final EventManager subject;

    public EventManagerProxy(EventManager subject) {
        this.subject = subject;
    }

    @Override
    public void register(@Nonnull Object listener) {
        this.subject.register(listener);
    }

    @Override
    public void handle(@Nonnull MessageReceivedEvent event) {
        try {
            subject.handle(event);
        } catch (RuntimeException e) {
            JDAImpl.LOG.error("The EventManager.handle() call had an uncaught exception", e);
        }
    }

}
