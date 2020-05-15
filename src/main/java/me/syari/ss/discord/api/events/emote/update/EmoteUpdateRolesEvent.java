

package me.syari.ss.discord.api.events.emote.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.List;


public class EmoteUpdateRolesEvent extends GenericEmoteUpdateEvent<List<Role>>
{
    public static final String IDENTIFIER = "roles";

    public EmoteUpdateRolesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote, @Nonnull List<Role> oldRoles)
    {
        super(api, responseNumber, emote, oldRoles, emote.getRoles(), IDENTIFIER);
    }


    @Nonnull
    public List<Role> getOldRoles()
    {
        return getOldValue();
    }


    @Nonnull
    public List<Role> getNewRoles()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public List<Role> getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public List<Role> getNewValue()
    {
        return super.getNewValue();
    }
}
