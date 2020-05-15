

package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link OrderAction OrderAction}
 * designed to modify the order of {@link Role Roles} of the
 * specified {@link Guild Guild}.
 * <br>To apply the changes you must finish the {@link RestAction RestAction}
 *
 * <p>Before you can use any of the {@code move} methods
 * you must use either {@link #selectPosition(Object) selectPosition(Role)} or {@link #selectPosition(int)}!
 *
 * <p><b>This uses descending order!</b>
 *
 * @since 3.0
 *
 * @see   Guild#modifyRolePositions()
 * @see   Guild#modifyRolePositions(boolean)
 */
public interface RoleOrderAction extends OrderAction<Role, RoleOrderAction>
{
    /**
     * The {@link Guild Guild} which holds
     * the roles from {@link #getCurrentOrder()}
     *
     * @return The corresponding {@link Guild Guild}
     */
    @Nonnull
    Guild getGuild();
}
