

package me.syari.ss.discord.api.events.emote.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;


public class EmoteUpdateNameEvent extends GenericEmoteUpdateEvent<String>
{
    public static final String IDENTIFIER = "name";

    public EmoteUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull Emote emote, @Nonnull String oldName)
    {
        super(api, responseNumber, emote, oldName, emote.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName()
    {
        return getOldValue();
    }


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
