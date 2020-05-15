

package me.syari.ss.discord.api.audio;

import me.syari.ss.discord.api.entities.User;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;


public interface AudioReceiveHandler
{

    AudioFormat OUTPUT_FORMAT = new AudioFormat(48000.0f, 16, 2, true, true);


    default boolean canReceiveCombined()
    {
        return false;
    }


    default boolean canReceiveUser()
    {
        return false;
    }


    default boolean canReceiveEncoded()
    {
        return false;
    }


    default void handleEncodedAudio(@Nonnull OpusPacket packet) {}


    default void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {}


    default void handleUserAudio(@Nonnull UserAudio userAudio) {}


    default boolean includeUserInCombinedAudio(@Nonnull User user)
    {
        return true;
    }
}
