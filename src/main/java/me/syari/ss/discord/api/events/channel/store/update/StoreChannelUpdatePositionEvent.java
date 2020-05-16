package me.syari.ss.discord.api.events.channel.store.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;


public class StoreChannelUpdatePositionEvent extends GenericStoreChannelUpdateEvent<Integer> {
    public static final String IDENTIFIER = "position";

    public StoreChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel, int prev) {
        super(api, responseNumber, channel, prev, channel.getPositionRaw(), IDENTIFIER);
    }


    public int getOldPosition() {
        return getOldValue();
    }


    public int getNewPosition() {
        return getNewValue();
    }
}
