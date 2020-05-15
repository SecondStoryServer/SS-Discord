

package me.syari.ss.discord.api.audio;

import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;


public class UserAudio
{
    protected User user;
    protected short[] audioData;

    public UserAudio(@Nonnull User user, @Nonnull short[] audioData)
    {
        this.user = user;
        this.audioData = audioData;
    }


    @Nonnull
    public User getUser()
    {
        return user;
    }


    @Nonnull
    public byte[] getAudioData(double volume)
    {
        return OpusPacket.getAudioData(audioData, volume);
    }
}
