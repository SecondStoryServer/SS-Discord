package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;


public class GuildVoiceLeaveEvent extends GenericGuildVoiceUpdateEvent {
    public GuildVoiceLeaveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull VoiceChannel channelLeft) {
        super(api, responseNumber, member, channelLeft, null);
    }

    @Nonnull
    @Override
    public VoiceChannel getChannelLeft() {
        return super.getChannelLeft();
    }

    @Nonnull
    @Override
    public VoiceChannel getOldValue() {
        return super.getOldValue();
    }
}
