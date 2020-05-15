

package net.dv8tion.jda.api.events.role.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} updated its color.
 *
 * <p>Can be used to retrieve the old color.
 *
 * <p>Identifier: {@code color}
 */
public class RoleUpdateColorEvent extends GenericRoleUpdateEvent<Integer>
{
    public static final String IDENTIFIER = "color";

    public RoleUpdateColorEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, int oldColor)
    {
        super(api, responseNumber, role, oldColor, role.getColorRaw(), IDENTIFIER);
    }

    /**
     * The old color
     *
     * @return The old color, or null
     */
    @Nullable
    public Color getOldColor()
    {
        return previous != Role.DEFAULT_COLOR_RAW ? new Color(previous) : null;
    }

    /**
     * The raw rgb value of the old color
     *
     * @return The raw rgb value of the old color
     */
    public int getOldColorRaw()
    {
        return getOldValue();
    }

    /**
     * The new color
     *
     * @return The new color, or null
     */
    @Nullable
    public Color getNewColor()
    {
        return next != Role.DEFAULT_COLOR_RAW ? new Color(next) : null;
    }

    /**
     * The raw rgb value of the new color
     *
     * @return The raw rgb value of the new color
     */
    public int getNewColorRaw()
    {
        return getNewValue();
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
