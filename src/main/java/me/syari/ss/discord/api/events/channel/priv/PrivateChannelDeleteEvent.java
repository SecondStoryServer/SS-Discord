package me.syari.ss.discord.api.events.channel.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public class PrivateChannelDeleteEvent extends Event {
    protected final PrivateChannel channel;

    public PrivateChannelDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull PrivateChannel channel) {
        super(api, responseNumber);
        this.channel = channel;
    }


    @Nonnull
    public User getUser() {
        return channel.getUser();
    }


    @Nonnull
    public PrivateChannel getChannel() {
        return channel;
    }
}
