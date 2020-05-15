

package net.dv8tion.jda.api.events.role.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} updated its position.
 *
 * <p>Can be used to retrieve the old position.
 *
 * <p>Identifier: {@code position}
 */
public class RoleUpdatePositionEvent extends GenericRoleUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "position";

    private final int oldPositionRaw;
    private final int newPositionRaw;

    public RoleUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldPosition, int oldPositionRaw)
    {
        super(api, responseNumber, role, oldPosition, role.getPosition(), IDENTIFIER);
        this.oldPositionRaw = oldPositionRaw;
        this.newPositionRaw = role.getPositionRaw();
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPosition()
    {
        return getOldValue();
    }

    /**
     * The old position
     *
     * @return The old position
     */
    public int getOldPositionRaw()
    {
        return oldPositionRaw;
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPosition()
    {
        return getNewValue();
    }

    /**
     * The new position
     *
     * @return The new position
     */
    public int getNewPositionRaw()
    {
        return newPositionRaw;
    }

    @Nonnull
    @Override
    public Integer getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public Integer getNewValue()
    {
        return super.getNewValue();
    }
}
