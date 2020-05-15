

package me.syari.ss.discord.annotations;

import java.lang.annotation.*;

/**
 * In combination with {@link Deprecated} specifies when this feature was marked as deprecated.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface DeprecatedSince
{
    String value();
}
