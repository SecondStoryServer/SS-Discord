

package me.syari.ss.discord.api.requests.restaction.pagination;

import me.syari.ss.discord.api.entities.ChannelType;
import me.syari.ss.discord.api.entities.Message;
import me.syari.ss.discord.api.entities.MessageChannel;

import javax.annotation.Nonnull;


public interface MessagePaginationAction extends PaginationAction<Message, MessagePaginationAction>
{
    
    @Nonnull
    default ChannelType getType()
    {
        return getChannel().getType();
    }

    
    @Nonnull
    MessageChannel getChannel();
}
