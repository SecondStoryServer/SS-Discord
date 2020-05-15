
package me.syari.ss.discord.api.hooks;

import me.syari.ss.discord.api.JDABuilder;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SubscribeEvent
{
}
