package me.syari.ss.discord.api.events.channel.text;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.TextChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericTextChannelEvent extends Event {
    private final TextChannel channel;

    public GenericTextChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull TextChannel channel) {
        super(api, responseNumber);
        this.channel = channel;
    }


    @Nonnull
    public TextChannel getChannel() {
        return channel;
    }


    @Nonnull
    public Guild getGuild() {
        return channel.getGuild();
    }
}
