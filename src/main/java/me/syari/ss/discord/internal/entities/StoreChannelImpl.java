

package me.syari.ss.discord.internal.entities;

import me.syari.ss.discord.api.entities.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class StoreChannelImpl extends AbstractChannelImpl<StoreChannel, StoreChannelImpl> implements StoreChannel
{
    public StoreChannelImpl(long id, GuildImpl guild)
    {
        super(id, guild);
    }

    @Override
    public StoreChannelImpl setPosition(int rawPosition)
    {
        getGuild().getStoreChannelView().clearCachedLists();
        return super.setPosition(rawPosition);
    }

    @Nonnull
    @Override
    public ChannelType getType()
    {
        return ChannelType.STORE;
    }

    @Nonnull
    @Override
    public List<Member> getMembers()
    {
        return Collections.emptyList();
    }

    @Override
    public String toString()
    {
        return "SC:" + getName() + '(' + getId() + ')';
    }
}
