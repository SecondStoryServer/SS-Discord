

package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;

import javax.annotation.Nonnull;

/**
 * {@link PaginationAction PaginationAction} that paginates the message history endpoint.
 * <br>Note that this implementation is not considered thread-safe as modifications to the cache are not done
 * with a lock. Calling methods on this class from multiple threads is not recommended.
 *
 * <p><b>Must provide not-null {@link MessageChannel MessageChannel} to compile a valid
 * pagination route.</b>
 *
 * <h2>Limits:</h2>
 * Minimum - 1
 * <br>Maximum - 100
 *
 * <h1>Example</h1>
 * <pre><code>
 * /**
 *  * Iterates messages in an async stream and stops once the limit has been reached.
 *  *&#47;
 * public static void onEachMessageAsync(MessageChannel channel, {@literal Consumer<Message>} consumer, int limit)
 * {
 *     if (limit{@literal <} 1)
 *         return;
 *     MessagePaginationAction action = channel.getIterableHistory();
 *     AtomicInteger counter = new AtomicInteger(limit);
 *     action.forEachAsync( (message){@literal ->}
 *     {
 *         consumer.accept(message);
 *         // if false the iteration is terminated; else it continues
 *         return counter.decrementAndGet() == 0;
 *     });
 * }
 * </code></pre>
 *
 * @since  3.1
 *
 * @see    MessageChannel#getIterableHistory()
 */
public interface MessagePaginationAction extends PaginationAction<Message, MessagePaginationAction>
{
    /**
     * The {@link ChannelType ChannelType} of
     * the targeted {@link MessageChannel MessageChannel}.
     *
     * @return {@link ChannelType ChannelType}
     */
    @Nonnull
    default ChannelType getType()
    {
        return getChannel().getType();
    }

    /**
     * The targeted {@link MessageChannel MessageChannel}
     *
     * @return The MessageChannel instance
     */
    @Nonnull
    MessageChannel getChannel();
}
