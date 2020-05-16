package me.syari.ss.discord.api.events.channel.priv;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.PrivateChannel;
import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public class PrivateChannelCreateEvent extends Event {
    private final PrivateChannel channel;

    public PrivateChannelCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull PrivateChannel channel) {
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
