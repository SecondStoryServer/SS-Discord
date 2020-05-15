

package me.syari.ss.discord.api.audio;

import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class CombinedAudio
{
    protected List<User> users;
    protected short[] audioData;

    public CombinedAudio(@Nonnull List<User> users, @Nonnull short[] audioData)
    {
        this.users = Collections.unmodifiableList(users);
        this.audioData = audioData;
    }


    @Nonnull
    public List<User> getUsers()
    {
        return users;
    }


    @Nonnull
    public byte[] getAudioData(double volume)
    {
        return OpusPacket.getAudioData(audioData, volume);
    }
}
