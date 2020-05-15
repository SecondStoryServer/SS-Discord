

package me.syari.ss.discord.api.events.emote.update;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.emote.GenericEmoteEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Emote;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericEmoteUpdateEvent<T> extends GenericEmoteEvent implements UpdateEvent<Emote, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericEmoteUpdateEvent(
            @Nonnull JDA api, long responseNumber, @Nonnull Emote emote,
            @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, emote);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public Emote getEntity()
    {
        return getEmote();
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return identifier;
    }

    @Nullable
    @Override
    public T getOldValue()
    {
        return previous;
    }

    @Nullable
    @Override
    public T getNewValue()
    {
        return next;
    }

    @Override
    public String toString()
    {
        return "EmoteUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
