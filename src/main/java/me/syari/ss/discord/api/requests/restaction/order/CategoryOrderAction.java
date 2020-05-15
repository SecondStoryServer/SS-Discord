

package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.entities.Category;
import me.syari.ss.discord.api.requests.RestAction;

import javax.annotation.Nonnull;

/**
 * An extension of {@link ChannelOrderAction ChannelOrderAction} with
 * similar functionality, but constrained to the bounds of a single {@link Category Category}.
 * <br>To apply the changes you must finish the {@link RestAction RestAction}.
 *
 * <p>Before you can use any of the {@code move} methods
 * you must use either {@link #selectPosition(Object) selectPosition(GuildChannel)} or {@link #selectPosition(int)}!
 *
 * @author Kaidan Gustave
 *
 * @see    Category#modifyTextChannelPositions()
 * @see    Category#modifyVoiceChannelPositions()
 */
public interface CategoryOrderAction extends ChannelOrderAction
{
    /**
     * Gets the {@link Category Category}
     * controlled by this CategoryOrderAction.
     *
     * @return The {@link Category Category}
     *         of this CategoryOrderAction.
     */
    @Nonnull
    Category getCategory();
}
