

package me.syari.ss.discord.annotations;

import java.lang.annotation.*;

/**
 * Functionality annotated with ForRemoval will no longer be supported
 * and should not be used anymore in new code.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ForRemoval
{
}
