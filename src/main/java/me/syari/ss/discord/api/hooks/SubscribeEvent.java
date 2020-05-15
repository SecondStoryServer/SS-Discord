
package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.JDABuilder;

import java.lang.annotation.*;

/**
 * Annotation used by the {@link AnnotatedEventManager AnnotatedEventManager}
 * this is only picked up if the event manager implementation has been set to use the {@link AnnotatedEventManager AnnotatedEventManager}
 * via {@link JDABuilder#setEventManager(IEventManager) JDABuilder.setEventManager(IEventManager)}
 *
 * @see AnnotatedEventManager
 * @see JDABuilder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SubscribeEvent
{
}
