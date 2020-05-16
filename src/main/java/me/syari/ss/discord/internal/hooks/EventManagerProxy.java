package me.syari.ss.discord.internal.hooks;

import me.syari.ss.discord.api.events.GenericEvent;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.InterfacedEventManager;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;
import java.util.List;

public class EventManagerProxy implements IEventManager {
    private IEventManager subject;

    public EventManagerProxy(IEventManager subject) {
        this.subject = subject;
    }

    public void setSubject(IEventManager subject) {
        this.subject = subject == null ? new InterfacedEventManager() : subject;
    }

    public IEventManager getSubject() {
        return subject;
    }

    @Override
    public void register(@Nonnull Object listener) {
        this.subject.register(listener);
    }

    @Override
    public void unregister(@Nonnull Object listener) {
        this.subject.unregister(listener);
    }

    @Override
    public void handle(@Nonnull GenericEvent event) {
        // don't allow mere exceptions to obstruct the socket handler
        try {
            subject.handle(event);
        } catch (RuntimeException e) {
            JDAImpl.LOG.error("The EventManager.handle() call had an uncaught exception", e);
        }
    }

    @Nonnull
    @Override
    public List<Object> getRegisteredListeners() {
        return subject.getRegisteredListeners();
    }
}
