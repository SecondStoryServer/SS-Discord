
package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;


@FunctionalInterface
public interface EventListener
{
    
    void onEvent(@Nonnull GenericEvent event);
}
