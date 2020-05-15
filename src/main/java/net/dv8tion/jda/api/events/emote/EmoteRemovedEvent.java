

package net.dv8tion.jda.api.events.emote;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;

import javax.annotation.Nonnull;

/**
 * Indicates that an {@link net.dv8tion.jda.api.entities.Emote Emote} was removed from a Guild.
 */
public class EmoteRemovedEvent extends GenericEmoteEvent
{
    public EmoteRemovedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber, emote);
    }
}
