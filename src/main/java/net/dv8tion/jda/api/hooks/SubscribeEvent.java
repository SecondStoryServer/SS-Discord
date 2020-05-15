
package net.dv8tion.jda.api.hooks;

import java.lang.annotation.*;

/**
 * Annotation used by the {@link net.dv8tion.jda.api.hooks.AnnotatedEventManager AnnotatedEventManager}
 * this is only picked up if the event manager implementation has been set to use the {@link net.dv8tion.jda.api.hooks.AnnotatedEventManager AnnotatedEventManager}
 * via {@link net.dv8tion.jda.api.JDABuilder#setEventManager(IEventManager) JDABuilder.setEventManager(IEventManager)}
 *
 * @see net.dv8tion.jda.api.hooks.AnnotatedEventManager
 * @see net.dv8tion.jda.api.JDABuilder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SubscribeEvent
{
}
