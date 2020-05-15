

package me.syari.ss.discord.api.audio.factory;

import me.syari.ss.discord.api.audio.AudioSendHandler;
import me.syari.ss.discord.api.audio.hooks.ConnectionStatus;
import me.syari.ss.discord.api.entities.VoiceChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


@NotThreadSafe
public interface IPacketProvider
{
    
    @Nonnull
    String getIdentifier();

    
    @Nonnull
    VoiceChannel getConnectedChannel();

    
    @Nonnull
    DatagramSocket getUdpSocket();

    
    @Nonnull
    InetSocketAddress getSocketAddress();

    
    @Nullable
    ByteBuffer getNextPacketRaw(boolean changeTalking);

    
    @Nullable
    DatagramPacket getNextPacket(boolean changeTalking);

    
    void onConnectionError(@Nonnull ConnectionStatus status);

    
    void onConnectionLost();
}
