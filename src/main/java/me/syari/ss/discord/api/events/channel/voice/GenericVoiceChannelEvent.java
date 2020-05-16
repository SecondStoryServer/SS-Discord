package me.syari.ss.discord.api.events.channel.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.Event;

import javax.annotation.Nonnull;


public abstract class GenericVoiceChannelEvent extends Event {
    private final VoiceChannel channel;

    public GenericVoiceChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel) {
        super(api, responseNumber);
        this.channel = channel;
    }


    @Nonnull
    public VoiceChannel getChannel() {
        return channel;
    }


    @Nonnull
    public Guild getGuild() {
        return channel.getGuild();
    }
}
