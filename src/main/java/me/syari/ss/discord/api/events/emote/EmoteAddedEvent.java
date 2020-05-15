

package me.syari.ss.discord.api.events.emote;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;


public class EmoteAddedEvent extends GenericEmoteEvent
{
    public EmoteAddedEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote)
    {
        super(api, responseNumber, emote);
    }
}
