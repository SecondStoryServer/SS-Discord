

package net.dv8tion.jda.api.events.emote;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;

import javax.annotation.Nonnull;

/**
 * Indicates that a new {@link net.dv8tion.jda.api.entities.Emote Emote} was added to a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 */
public class EmoteAddedEvent extends GenericEmoteEvent
{
    public EmoteAddedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber, emote);
    }
}
