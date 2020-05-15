
package me.syari.ss.discord.api.events.guild.update;

import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.guild.GenericGuildEvent;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Indicates that a {@link Guild Guild} was updated.
 *
 * <p>Can be used to detect when a Guild is updated.
 */
public abstract class GenericGuildUpdateEvent<T> extends GenericGuildEvent implements UpdateEvent<Guild, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericGuildUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull Guild guild,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, guild);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public Guild getEntity()
    {
        return getGuild();
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
        return "GuildUpdate[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ')';
    }
}
