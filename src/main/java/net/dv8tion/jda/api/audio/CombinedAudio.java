

package net.dv8tion.jda.api.audio;

import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Represents a packet of combined audio data from 0 to n Users.
 */
public class CombinedAudio
{
    protected List<User> users;
    protected short[] audioData;

    public CombinedAudio(@Nonnull List<User> users, @Nonnull short[] audioData)
    {
        this.users = Collections.unmodifiableList(users);
        this.audioData = audioData;
    }

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.api.entities.User Users} that provided audio that was combined.
     * <br>Basically: This is a list of all users that can be heard in the data returned by {@link #getAudioData(double)}
     * <p>
     * <b>NOTE: If no users were speaking, this list is empty and {@link #getAudioData(double)} provides silent audio data.</b>
     *
     * @return Never-null list of all users that provided audio.
     */
    @Nonnull
    public List<User> getUsers()
    {
        return users;
    }

    /**
     * Provides 20 Milliseconds of combined audio data in 48KHz 16bit stereo signed BigEndian PCM.
     * <br>Format defined by: {@link net.dv8tion.jda.api.audio.AudioReceiveHandler#OUTPUT_FORMAT AudioReceiveHandler.OUTPUT_FORMAT}.
     * <p>
     * The output volume of the data can be modified by the provided {@code `volume`} parameter. {@code `1.0`} is considered to be 100% volume.
     * <br>Going above `{@code 1.0`} can increase the volume further, but you run the risk of audio distortion.
     * <p>
     * <b>NOTE: If no users were speaking, this provides silent audio and {@link #getUsers()} returns an empty list!</b>
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
