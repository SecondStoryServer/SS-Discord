

package net.dv8tion.jda.api.events.role.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} updated its hoist state.
 *
 * <p>Can be used to retrieve the hoist state.
 *
 * <p>Identifier: {@code hoist}
 */
public class RoleUpdateHoistedEvent extends GenericRoleUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "hoist";

    public RoleUpdateHoistedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasHoisted)
    {
        super(api, responseNumber, role, wasHoisted, !wasHoisted, IDENTIFIER);
    }

    /**
     * Whether the role was hoisted
     *
     * @return True, if the role was hoisted before this update
     */
    public boolean wasHoisted()
    {
        return getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Boolean getNewValue()
    {
        return super.getNewValue();
    }
}
