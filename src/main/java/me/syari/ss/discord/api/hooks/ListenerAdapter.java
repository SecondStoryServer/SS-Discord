package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.MessageReceivedEvent;

import javax.annotation.Nonnull;


public interface ListenerAdapter {
    void onMessageReceived(@Nonnull MessageReceivedEvent event);
}
