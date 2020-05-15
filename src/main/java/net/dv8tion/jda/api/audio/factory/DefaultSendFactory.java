

package net.dv8tion.jda.api.audio.factory;

import javax.annotation.Nonnull;

/**
 * The default implementation of the {@link net.dv8tion.jda.api.audio.factory.IAudioSendFactory IAudioSendFactory}.
 */
public class DefaultSendFactory implements IAudioSendFactory
{
    @Nonnull
    @Override
    public IAudioSendSystem createSendSystem(@Nonnull IPacketProvider packetProvider)
    {
        return new DefaultSendSystem(packetProvider);
    }
}
