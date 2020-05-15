

package me.syari.ss.discord.api.events.emote;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that an {@link Emote Emote} was created/removed/updated.
 */
public abstract class GenericEmoteEvent extends Event
{
    protected final Emote emote;

    public GenericEmoteEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber);
        this.emote = emote;
    }

    /**
     * The {@link Guild Guild} where the emote came from
     *
     * @return The origin Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return emote.getGuild();
    }

    /**
     * The responsible {@link Emote Emote} for this event
     *
     * @return The emote
     */
    @Nonnull
    public Emote getEmote()
    {
        return emote;
    }

    /**
     * Whether this emote is managed by an integration
     *
     * @return True, if this emote is managed by an integration
     */
    public boolean isManaged()
    {
        return emote.isManaged();
    }
}
