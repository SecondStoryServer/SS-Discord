

package net.dv8tion.jda.api.audio.factory;

import javax.annotation.Nonnull;

/**
 * Factory interface for the creation of new {@link net.dv8tion.jda.api.audio.factory.IAudioSendSystem IAudioSendSystem} objects.
 * <br>JDA, by default, uses {@link net.dv8tion.jda.api.audio.factory.DefaultSendFactory DefaultSendFactory} for the
 * creation of its UDP audio packet sending system.
 * <p>
 * Implementations of this interface are provided to
 * {@link net.dv8tion.jda.api.JDABuilder#setAudioSendFactory(IAudioSendFactory) JDABuilder.setAudioSendFactory(IAudioSendFactory)}.
 */
public interface IAudioSendFactory
{
    /**
     * Called by JDA's audio system when a new {@link net.dv8tion.jda.api.audio.factory.IAudioSendSystem IAudioSendSystem}
     * instance is needed to handle the sending of UDP audio packets to discord.
     *
     * @param  packetProvider
     *         The connection provided to the new {@link net.dv8tion.jda.api.audio.factory.IAudioSendSystem IAudioSendSystem}
     *         object for proper setup and usage.
     *
     * @return The newly constructed IAudioSendSystem, ready for {@link IAudioSendSystem#start()} to be called.
     */
    @Nonnull
    IAudioSendSystem createSendSystem(@Nonnull IPacketProvider packetProvider);
}
