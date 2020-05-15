

package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageReaction;
import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;

/**
 * {@link PaginationAction PaginationAction} that paginates the reaction users endpoint.
 * <br>Note that this implementation is not considered thread-safe as modifications to the cache are not done
 * with a lock. Calling methods on this class from multiple threads is not recommended.
 *
 * <p><b>Must provide not-null {@link MessageReaction MessageReaction} to compile a valid
 * pagination route.</b>
 *
 * <h2>Limits:</h2>
 * Minimum - 1
 * <br>Maximum - 100
 *
 * <h1>Example</h1>
 * <pre>{@code
 * // Remove reactions for the specified emoji
 * public static void removeReaction(Message message, String emoji) {
 *     // get paginator
 *     ReactionPaginationAction users = message.retrieveReactionUsers(emoji);
 *     // remove reaction for every user
 *     users.forEachAsync((user) ->
 *         message.removeReaction(emoji, user).queue()
 *     );
 * }
 * }</pre>
 *
 * @since  3.1
 *
 * @see    MessageReaction#retrieveUsers()
 * @see    Message#retrieveReactionUsers(String)
 * @see    Message#retrieveReactionUsers(Emote)
 */
public interface ReactionPaginationAction extends PaginationAction<User, ReactionPaginationAction>
{
    /**
     * The current target {@link MessageReaction MessageReaction}
     *
     * @throws IllegalStateException
     *         If this was created by {@link Message#retrieveReactionUsers(Emote) Message.retrieveReactionUsers(...)}
     *
     * @return The current MessageReaction
     */
    @Nonnull
    MessageReaction getReaction();
}
