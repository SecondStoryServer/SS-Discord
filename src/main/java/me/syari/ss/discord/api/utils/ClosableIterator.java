

package me.syari.ss.discord.api.utils;

import java.util.Iterator;

/**
 * Iterator holding a resource that must be free'd by the consumer.
 * <br>Close is an idempotent function and can be performed multiple times without effects beyond first invocation.
 *
 * <p>This closes automatically when {@link #hasNext()} returns {@code false} but
 * its recommended to only be used within a {@code try-with-resources} block for safety.
 *
 * <h3>Example</h3>
 * This can handle any exceptions thrown while iterating and ensures the lock is released correctly.
 * <pre>{@code
 * try (ClosableIterator<T> it = cacheView.lockedIterator()) {
 *     while (it.hasNext()) {
 *         consume(it.next());
 *     }
 * }
 * }</pre>
 *
 * @param <T>
 *        The element type
 *
 * @since 4.0.0
 */
public interface ClosableIterator<T> extends Iterator<T>, AutoCloseable
{
    @Override
    void close();
}
