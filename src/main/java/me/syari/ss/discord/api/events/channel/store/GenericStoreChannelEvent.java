package me.syari.ss.discord.api.events.channel.store;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericStoreChannelEvent extends Event {
    protected final StoreChannel channel;

    public GenericStoreChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel) {
        super(api, responseNumber);
        this.channel = channel;
    }


    @Nonnull
    public StoreChannel getChannel() {
        return channel;
    }
}
