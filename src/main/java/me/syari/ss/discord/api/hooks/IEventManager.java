package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.events.MessageReceivedEvent;

import javax.annotation.Nonnull;


public interface IEventManager {

    void register(@Nonnull Object listener);


    void handle(@Nonnull MessageReceivedEvent event);


}
