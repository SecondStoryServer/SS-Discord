

/**
 * EventManager and EventListener implementations and interfaces.
 *
 * <p>Every JDA instance has an {@link net.dv8tion.jda.api.hooks.IEventManager EventManager} implementation
 * that deals with the handling and forwarding of {@link net.dv8tion.jda.api.events.Event Events}.
 *
 * <p>The default manager is the {@link net.dv8tion.jda.api.hooks.InterfacedEventManager InterfacedEventManager}
 * which uses the {@link net.dv8tion.jda.api.hooks.EventListener EventListener} to listen for events.
 * <br>The {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter} is an implementation which provides
 * methods for each event of {@link net.dv8tion.jda.api.events}
 *
 * <p>The {@link net.dv8tion.jda.api.hooks.AnnotatedEventManager AnnotatedEventManager}
 * can forward events directly to methods that have the {@link net.dv8tion.jda.api.hooks.SubscribeEvent SubscribeEvent} annotation.
 *
 * <p><b>Note: All of the standard EventManager implementations are single-threaded</b>
 */
package net.dv8tion.jda.api.hooks;
