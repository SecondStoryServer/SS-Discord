

package me.syari.ss.discord.api.events.emote.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Role;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Indicates that the role whitelist for an {@link Emote Emote} changed.
 *
 * <p>Can be used to retrieve the old role whitelist
 *
 * <p>Identifier: {@code roles}
 */
public class EmoteUpdateRolesEvent extends GenericEmoteUpdateEvent<List<Role>>
{
    public static final String IDENTIFIER = "roles";

    public EmoteUpdateRolesEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote, @Nonnull List<Role> oldRoles)
    {
        super(api, responseNumber, emote, oldRoles, emote.getRoles(), IDENTIFIER);
    }

    /**
     * The old role whitelist
     *
     * @return The old role whitelist
     */
    @Nonnull
    public List<Role> getOldRoles()
    {
        return getOldValue();
    }

    /**
     * The new role whitelist
     *
     * @return The new role whitelist
     */
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
