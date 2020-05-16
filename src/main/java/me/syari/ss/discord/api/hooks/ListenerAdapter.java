package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;


public abstract class ListenerAdapter {
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    }
}
