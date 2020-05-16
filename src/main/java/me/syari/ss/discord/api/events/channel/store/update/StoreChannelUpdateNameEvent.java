package me.syari.ss.discord.api.events.channel.store.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.StoreChannel;

import javax.annotation.Nonnull;


public class StoreChannelUpdateNameEvent extends GenericStoreChannelUpdateEvent<String> {
    public static final String IDENTIFIER = "name";

    public StoreChannelUpdateNameEvent(@Nonnull JDA api, long responseNumber, @Nonnull StoreChannel channel,
                                       @Nonnull String prev) {
        super(api, responseNumber, channel, prev, channel.getName(), IDENTIFIER);
    }


    @Nonnull
    public String getOldName() {
        return getOldValue();
    }


    @Nonnull
    public String getNewName() {
        return getNewValue();
    }

    @Nonnull
    @Override
    public String getOldValue() {
        return super.getOldValue();
    }

    @Nonnull
    @Override
    public String getNewValue() {
        return super.getNewValue();
    }
}
