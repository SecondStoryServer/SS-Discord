

package net.dv8tion.jda.api.events.role.update;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nonnull;

/**
 * Indicates that a {@link net.dv8tion.jda.api.entities.Role Role} updated its mentionable state.
 *
 * <p>Can be used to retrieve the old mentionable state.
 *
 * <p>Identifier: {@code mentionable}
 */
public class RoleUpdateMentionableEvent extends GenericRoleUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "mentionable";

    public RoleUpdateMentionableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasMentionable)
    {
        super(api, responseNumber, role, wasMentionable, !wasMentionable, IDENTIFIER);
    }

    /**
     * Whether the role was mentionable
     *
     * @return True, if this role was mentionable before this update
     */
    public boolean wasMentionable()
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
