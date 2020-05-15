
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * Represents the connection used for direct messaging.
 *
 * @see User#openPrivateChannel()
 */
public interface PrivateChannel extends MessageChannel, IFakeable
{
    /**
     * The {@link User User} that this {@link PrivateChannel PrivateChannel} communicates with.
     *
     * @return A non-null {@link User User}.
     */
    @Nonnull
    User getUser();

    /**
     * Returns the {@link JDA JDA} instance of this PrivateChannel
     *
     * @return the corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Closes a PrivateChannel. After being closed successfully the PrivateChannel is removed from the JDA mapping.
     * <br>As a note, this does not remove the history of the PrivateChannel. If the channel is reopened the history will
     * still be present.
     *
     * @return {@link RestAction RestAction} - Type: Void
     */
    @Nonnull
    @CheckReturnValue
    RestAction<Void> close();
}
