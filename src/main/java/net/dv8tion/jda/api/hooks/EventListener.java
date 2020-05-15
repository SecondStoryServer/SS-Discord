
package net.dv8tion.jda.api.hooks;

import net.dv8tion.jda.api.events.GenericEvent;

import javax.annotation.Nonnull;

/**
 * JDA pushes {@link net.dv8tion.jda.api.events.GenericEvent GenericEvents} to the registered EventListeners.
 *
 * <p>Register an EventListener with either a {@link net.dv8tion.jda.api.JDA JDA} object
 * <br>or the {@link net.dv8tion.jda.api.JDABuilder JDABuilder}.
 *
 * <p><b>Examples: </b>
 * <br>
 * <code>
 *     JDA jda = new {@link net.dv8tion.jda.api.JDABuilder JDABuilder}("token").{@link net.dv8tion.jda.api.JDABuilder#addEventListeners(Object...) addEventListeners(listeners)}.{@link net.dv8tion.jda.api.JDABuilder#build() build()};<br>
 *     {@link net.dv8tion.jda.api.JDA#addEventListener(Object...) jda.addEventListener(listeners)};
 * </code>
 *
 * @see net.dv8tion.jda.api.hooks.ListenerAdapter
 * @see net.dv8tion.jda.api.hooks.InterfacedEventManager
 */
@FunctionalInterface
public interface EventListener
{
    /**
     * Handles any {@link net.dv8tion.jda.api.events.GenericEvent GenericEvent}.
     *
     * <p>To get specific events with Methods like {@code onMessageReceived(MessageReceivedEvent event)}
     * take a look at: {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter}
     *
     * @param  event
     *         The Event to handle.
     */
    void onEvent(@Nonnull GenericEvent event);
}
