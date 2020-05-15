

package net.dv8tion.jda.api.events.emote;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;

/**
 * Indicates that an {@link net.dv8tion.jda.api.entities.Emote Emote} was created/removed/updated.
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
     * The {@link net.dv8tion.jda.api.entities.Guild Guild} where the emote came from
     *
     * @return The origin Guild
     */
    @Nonnull
    public Guild getGuild()
    {
        return emote.getGuild();
    }

    /**
     * The responsible {@link net.dv8tion.jda.api.entities.Emote Emote} for this event
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
