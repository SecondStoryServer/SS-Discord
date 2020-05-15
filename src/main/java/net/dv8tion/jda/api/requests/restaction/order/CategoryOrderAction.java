

package net.dv8tion.jda.api.requests.restaction.order;

import net.dv8tion.jda.api.entities.Category;

import javax.annotation.Nonnull;

/**
 * An extension of {@link ChannelOrderAction ChannelOrderAction} with
 * similar functionality, but constrained to the bounds of a single {@link net.dv8tion.jda.api.entities.Category Category}.
 * <br>To apply the changes you must finish the {@link net.dv8tion.jda.api.requests.RestAction RestAction}.
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
     * Gets the {@link net.dv8tion.jda.api.entities.Category Category}
     * controlled by this CategoryOrderAction.
     *
     * @return The {@link net.dv8tion.jda.api.entities.Category Category}
     *         of this CategoryOrderAction.
     */
    @Nonnull
    Category getCategory();
}
