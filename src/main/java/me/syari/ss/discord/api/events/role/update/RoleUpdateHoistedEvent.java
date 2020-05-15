

package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleUpdateHoistedEvent extends GenericRoleUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "hoist";

    public RoleUpdateHoistedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasHoisted)
    {
        super(api, responseNumber, role, wasHoisted, !wasHoisted, IDENTIFIER);
    }


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
