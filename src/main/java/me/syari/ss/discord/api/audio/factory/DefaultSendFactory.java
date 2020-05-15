

package me.syari.ss.discord.api.audio.factory;

import javax.annotation.Nonnull;


public class DefaultSendFactory implements IAudioSendFactory
{
    @Nonnull
    @Override
    public IAudioSendSystem createSendSystem(@Nonnull IPacketProvider packetProvider)
    {
        return new DefaultSendSystem(packetProvider);
    }
}
