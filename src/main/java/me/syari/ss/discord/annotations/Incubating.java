

package me.syari.ss.discord.annotations;

import java.lang.annotation.*;

/**
 * Functionality annotated with Incubating might change in a future release.
 * This means the binary interface or similar changes could disrupt the updating process.
 *
 * <p>This will often be done for API features that are not officially released to bots or in general, yet.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Incubating
{
}
