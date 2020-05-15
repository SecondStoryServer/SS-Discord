

package net.dv8tion.jda.api.audio;

import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;

/**
 * Represents a packet of User specific audio.
 */
public class UserAudio
{
    protected User user;
    protected short[] audioData;

    public UserAudio(@Nonnull User user, @Nonnull short[] audioData)
    {
        this.user = user;
        this.audioData = audioData;
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.User User} that provided the audio data.
     *
     * @return Never-null {@link net.dv8tion.jda.api.entities.User User} object.
     */
    @Nonnull
    public User getUser()
    {
        return user;
    }

    /**
     * Provides 20 Milliseconds of combined audio data in 48KHz 16bit stereo signed BigEndian PCM.
     * <br>Format defined by: {@link net.dv8tion.jda.api.audio.AudioReceiveHandler#OUTPUT_FORMAT AudioReceiveHandler.OUTPUT_FORMAT}.
     * <p>
     * The output volume of the data can be modified by the provided {@code `volume`} parameter. {@code `1.0`} is considered to be 100% volume.
     * <br>Going above {@code `1.0`} can increase the volume further, but you run the risk of audio distortion.
     *
     * @param  volume
     *         Value used to modify the "volume" of the returned audio data. 1.0 is normal volume.
     *
     * @return Never-null byte array of PCM data defined by {@link net.dv8tion.jda.api.audio.AudioReceiveHandler#OUTPUT_FORMAT AudioReceiveHandler.OUTPUT_FORMAT}
     */
    @Nonnull
    public byte[] getAudioData(double volume)
    {
        return OpusPacket.getAudioData(audioData, volume);
    }
}
