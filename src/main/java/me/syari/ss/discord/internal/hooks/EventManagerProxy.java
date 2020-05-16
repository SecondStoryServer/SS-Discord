package me.syari.ss.discord.internal.hooks;

import me.syari.ss.discord.api.events.message.MessageReceivedEvent;
import me.syari.ss.discord.api.hooks.IEventManager;
import me.syari.ss.discord.api.hooks.InterfacedEventManager;
import me.syari.ss.discord.internal.JDAImpl;

import javax.annotation.Nonnull;

public class EventManagerProxy implements IEventManager {
    private IEventManager subject;

    public EventManagerProxy(IEventManager subject) {
        this.subject = subject;
    }

    public void setSubject(IEventManager subject) {
        this.subject = subject == null ? new InterfacedEventManager() : subject;
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
