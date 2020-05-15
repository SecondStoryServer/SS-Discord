
package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.JDABuilder;
import me.syari.ss.discord.api.events.GenericEvent;

import javax.annotation.Nonnull;

/**
 * JDA pushes {@link GenericEvent GenericEvents} to the registered EventListeners.
 *
 * <p>Register an EventListener with either a {@link JDA JDA} object
 * <br>or the {@link JDABuilder JDABuilder}.
 *
 * <p><b>Examples: </b>
 * <br>
 * <code>
 *     JDA jda = new {@link JDABuilder JDABuilder}("token").{@link JDABuilder#addEventListeners(Object...) addEventListeners(listeners)}.{@link JDABuilder#build() build()};<br>
 *     {@link JDA#addEventListener(Object...) jda.addEventListener(listeners)};
 * </code>
 *
 * @see ListenerAdapter
 * @see InterfacedEventManager
 */
@FunctionalInterface
public interface EventListener
{
    /**
     * Handles any {@link GenericEvent GenericEvent}.
     *
     * <p>To get specific events with Methods like {@code onMessageReceived(MessageReceivedEvent event)}
     * take a look at: {@link ListenerAdapter ListenerAdapter}
     *
     * @param  event
     *         The Event to handle.
     */
    void onEvent(@Nonnull GenericEvent event);
}
