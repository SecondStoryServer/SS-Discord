

package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
    public int getPosition()
    {
        List<GuildChannel> channels = new ArrayList<>(getGuild().getTextChannels());
        channels.addAll(getGuild().getStoreChannels());
        Collections.sort(channels);
        for (int i = 0; i < channels.size(); i++)
        {
            if (equals(channels.get(i)))
                return i;
        }
        throw new AssertionError("Somehow when determining position we never found the StoreChannel in the Guild's channels? wtf?");
    }

    @Nonnull
    @Override
    public ChannelAction<StoreChannel> createCopy(@Nonnull Guild guild)
    {
        throw new UnsupportedOperationException("Bots cannot create store channels");
    }

    @Override
    public String toString()
    {
        return "SC:" + getName() + '(' + getId() + ')';
    }
}
