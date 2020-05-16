package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.GenericEvent;
import me.syari.ss.discord.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;


public abstract class ListenerAdapter implements EventListener {
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    }

    @Override
    public final void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof MessageReceivedEvent)
            onMessageReceived((MessageReceivedEvent) event);
    }
}
