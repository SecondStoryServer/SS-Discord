package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class VoiceChannelUpdatePositionEvent extends GenericVoiceChannelUpdateEvent<Integer> {
    public static final String IDENTIFIER = "position";

    public VoiceChannelUpdatePositionEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldPosition) {
        super(api, responseNumber, channel, oldPosition, channel.getPositionRaw(), IDENTIFIER);
    }


    public int getOldPosition() {
        return getOldValue();
    }


    public int getNewPosition() {
        return getNewValue();
    }
}
