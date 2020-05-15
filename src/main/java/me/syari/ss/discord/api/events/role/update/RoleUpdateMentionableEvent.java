

package me.syari.ss.discord.api.events.role.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;


public class RoleUpdateMentionableEvent extends GenericRoleUpdateEvent<Boolean>
{
    public static final String IDENTIFIER = "mentionable";

    public RoleUpdateMentionableEvent(@Nonnull JDA api, long responseNumber, @Nonnull Role role, boolean wasMentionable)
    {
        super(api, responseNumber, role, wasMentionable, !wasMentionable, IDENTIFIER);
    }


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
