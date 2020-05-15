

package me.syari.ss.discord.api.audio.factory;

import me.syari.ss.discord.api.JDABuilder;

import javax.annotation.Nonnull;


public interface IAudioSendFactory
{
    
    @Nonnull
    IAudioSendSystem createSendSystem(@Nonnull IPacketProvider packetProvider);
}
