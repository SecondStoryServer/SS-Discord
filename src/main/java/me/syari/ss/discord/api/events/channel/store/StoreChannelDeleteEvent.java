

package me.syari.ss.discord.api.events.channel.store;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;


public class StoreChannelDeleteEvent extends GenericStoreChannelEvent
{
    public StoreChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel)
    {
        super(api, responseNumber, channel);
    }
}
