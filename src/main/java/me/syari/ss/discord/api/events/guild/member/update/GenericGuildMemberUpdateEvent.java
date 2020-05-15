
package me.syari.ss.discord.api.events.guild.member.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.events.UpdateEvent;
import me.syari.ss.discord.api.events.guild.member.GenericGuildMemberEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class GenericGuildMemberUpdateEvent<T> extends GenericGuildMemberEvent implements UpdateEvent<Member, T>
{
    protected final T previous;
    protected final T next;
    protected final String identifier;

    public GenericGuildMemberUpdateEvent(
        @Nonnull JDA api, long responseNumber, @Nonnull Member member,
        @Nullable T previous, @Nullable T next, @Nonnull String identifier)
    {
        super(api, responseNumber, member);
        this.previous = previous;
        this.next = next;
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public String getPropertyIdentifier()
    {
        return identifier;
    }

    @Nonnull
    @Override
    public Member getEntity()
    {
        return getMember();
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
        return "GenericGuildMemberUpdateEvent[" + getPropertyIdentifier() + "](" + getOldValue() + "->" + getNewValue() + ")";
    }
}
