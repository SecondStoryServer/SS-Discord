

package net.dv8tion.jda.api.requests.restaction.order;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link OrderAction OrderAction}
 * designed to modify the order of {@link net.dv8tion.jda.api.entities.Role Roles} of the
 * specified {@link net.dv8tion.jda.api.entities.Guild Guild}.
 * <br>To apply the changes you must finish the {@link net.dv8tion.jda.api.requests.RestAction RestAction}
 *
 * <p>Before you can use any of the {@code move} methods
 * you must use either {@link #selectPosition(Object) selectPosition(Role)} or {@link #selectPosition(int)}!
 *
 * <p><b>This uses descending order!</b>
 *
 * @since 3.0
 *
 * @see   net.dv8tion.jda.api.entities.Guild#modifyRolePositions()
 * @see   net.dv8tion.jda.api.entities.Guild#modifyRolePositions(boolean)
 */
public interface RoleOrderAction extends OrderAction<Role, RoleOrderAction>
{
    /**
     * The {@link net.dv8tion.jda.api.entities.Guild Guild} which holds
     * the roles from {@link #getCurrentOrder()}
     *
     * @return The corresponding {@link net.dv8tion.jda.api.entities.Guild Guild}
     */
    @Nonnull
    Guild getGuild();
}
