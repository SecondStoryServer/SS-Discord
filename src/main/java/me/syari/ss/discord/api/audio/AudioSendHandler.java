

package me.syari.ss.discord.api.audio;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;


public interface AudioSendHandler
{

    AudioFormat INPUT_FORMAT = new AudioFormat(48000f, 16, 2, true, true);


    boolean canProvide();


    @Nullable
    ByteBuffer provide20MsAudio();


    default boolean isOpus()
    {
        return false;
    }
}
