

/**
 * EventManager and EventListener implementations and interfaces.
 *
 * <p>Every JDA instance has an {@link me.syari.ss.discord.api.hooks.IEventManager EventManager} implementation
 * that deals with the handling and forwarding of {@link me.syari.ss.discord.api.events.Event Events}.
 *
 * <p>The default manager is the {@link me.syari.ss.discord.api.hooks.InterfacedEventManager InterfacedEventManager}
 * which uses the {@link me.syari.ss.discord.api.hooks.EventListener EventListener} to listen for events.
 * <br>The {@link me.syari.ss.discord.api.hooks.ListenerAdapter ListenerAdapter} is an implementation which provides
 * methods for each event of {@link me.syari.ss.discord.api.events}
 *
 * <p>The {@link me.syari.ss.discord.api.hooks.AnnotatedEventManager AnnotatedEventManager}
 * can forward events directly to methods that have the {@link me.syari.ss.discord.api.hooks.SubscribeEvent SubscribeEvent} annotation.
 *
 * <p><b>Note: All of the standard EventManager implementations are single-threaded</b>
 */
package me.syari.ss.discord.api.hooks;
