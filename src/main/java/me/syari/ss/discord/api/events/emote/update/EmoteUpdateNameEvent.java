

package me.syari.ss.discord.api.events.emote.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;

/**
 * Indicates that the name of an {@link Emote Emote} changed.
 *
 * <p>Can be used to retrieve the old name
 *
 * <p>Identifier: {@code name}
 */
public class EmoteUpdateNameEvent extends GenericEmoteUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public EmoteUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote, @Nonnull String oldName)
    {
        super(api, responseNumber, emote, oldName, emote.getName(), IDENTIFIER);
    }

    /**
     * The old name
     *
     * @return The old name
     */
    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }

    /**
     * The new name
     *
     * @return The new name
     */
    @Nonnull
    public String getNewName()
    {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue()
    {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue()
    {
        return super.getNewValue();
    }
}
