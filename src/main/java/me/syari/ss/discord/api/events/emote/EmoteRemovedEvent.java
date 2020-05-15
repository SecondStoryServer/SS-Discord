

package me.syari.ss.discord.api.events.emote;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;


public class EmoteRemovedEvent extends GenericEmoteEvent
{
    public EmoteRemovedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber, emote);
    }
}
