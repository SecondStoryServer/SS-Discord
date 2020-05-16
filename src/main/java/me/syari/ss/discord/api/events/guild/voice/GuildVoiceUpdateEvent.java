package me.syari.ss.discord.api.events.guild.voice;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.events.UpdateEvent;

import javax.annotation.Nullable;


public interface GuildVoiceUpdateEvent extends UpdateEvent<Member, VoiceChannel> {
    String IDENTIFIER = "voice-channel";


    @Nullable
    VoiceChannel getChannelLeft();


    @Nullable
    VoiceChannel getChannelJoined();
}
