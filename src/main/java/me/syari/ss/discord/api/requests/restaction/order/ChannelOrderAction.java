

package me.syari.ss.discord.api.requests.restaction.order;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildChannel;

import javax.annotation.Nonnull;
import java.util.EnumSet;


public interface ChannelOrderAction extends OrderAction<GuildChannel, ChannelOrderAction>
{
    
    @Nonnull
    Guild getGuild();

    
    int getSortBucket();

    
    @Nonnull
    default EnumSet<ChannelType> getChannelTypes()
    {
        return ChannelType.fromSortBucket(getSortBucket());
    }
}
