package me.syari.ss.discord.api.events.channel.voice.update;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class VoiceChannelUpdateUserLimitEvent extends GenericVoiceChannelUpdateEvent<Integer> {
    public static final String IDENTIFIER = "userlimit";

    public VoiceChannelUpdateUserLimitEvent(@Nonnull JDA api, long responseNumber, @Nonnull VoiceChannel channel, int oldUserLimit) {
        super(api, responseNumber, channel, oldUserLimit, channel.getUserLimit(), IDENTIFIER);
    }


    public int getOldUserLimit() {
        return getOldValue();
    }


    public int getNewUserLimit() {
        return getNewValue();
    }
}
