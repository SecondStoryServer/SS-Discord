

package me.syari.ss.discord.api.events.emote;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;

/**
 * Indicates that an {@link Emote Emote} was removed from a Guild.
 */
public class EmoteRemovedEvent extends GenericEmoteEvent
{
    public EmoteRemovedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber, emote);
    }
}
