

package me.syari.ss.discord.api.audio;

import me.syari.ss.discord.api.entities.User;
import me.syari.ss.discord.internal.audio.AudioPacket;
import me.syari.ss.discord.internal.audio.Decoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;


public final class OpusPacket implements Comparable<OpusPacket>
{

    public static final int OPUS_SAMPLE_RATE = 48000;

    public static final int OPUS_FRAME_SIZE = 960;

    public static final int OPUS_FRAME_TIME_AMOUNT = 20;

    public static final int OPUS_CHANNEL_COUNT = 2;

    private final long userId;
    private final byte[] opusAudio;
    private final Decoder decoder;
    private final AudioPacket rawPacket;

    private short[] decoded;
    private boolean triedDecode;

    public OpusPacket(@Nonnull AudioPacket packet, long userId, @Nullable Decoder decoder)
    {
        this.rawPacket = packet;
        this.userId = userId;
        this.decoder = decoder;
        this.opusAudio = packet.getEncodedAudio().array();
    }

    
    public char getSequence()
    {
        return rawPacket.getSequence();
    }

    
    public int getTimestamp()
    {
        return rawPacket.getTimestamp();
    }

    
    public int getSSRC()
    {
        return rawPacket.getSSRC();
    }

    
    public long getUserId()
    {
        return userId;
    }

    
    public boolean canDecode()
    {
        return decoder != null && decoder.isInOrder(getSequence());
    }

    
    @Nonnull
    public byte[] getOpusAudio()
    {
        //prevent write access to backing array
        return Arrays.copyOf(opusAudio, opusAudio.length);
    }

    
    @Nullable
    public synchronized short[] decode()
    {
        if (triedDecode)
            return decoded;
        if (decoder == null)
            throw new IllegalStateException("No decoder available");
        if (!decoder.isInOrder(getSequence()))
            throw new IllegalStateException("Packet is not in order");
        triedDecode = true;
        return decoded = decoder.decodeFromOpus(rawPacket); // null if failed to decode
    }

    
    @Nonnull
    @SuppressWarnings("ConstantConditions") // the null case is handled with an exception
    public byte[] getAudioData(double volume)
    {
        return getAudioData(decode(), volume); // throws IllegalArgument if decode failed
    }

    
    @Nonnull
    @SuppressWarnings("ConstantConditions") // the null case is handled with an exception
    public static byte[] getAudioData(@Nonnull short[] decoded, double volume)
    {
        if (decoded == null)
            throw new IllegalArgumentException("Cannot get audio data from null");
        int byteIndex = 0;
        byte[] audio = new byte[decoded.length * 2];
        for (short s : decoded)
        {
            if (volume != 1.0)
                s = (short) (s * volume);

            byte leftByte  = (byte) ((s >>> 8) & 0xFF);
            byte rightByte = (byte)  (s        & 0xFF);
            audio[byteIndex] = leftByte;
            audio[byteIndex + 1] = rightByte;
            byteIndex += 2;
        }
        return audio;
    }

    @Override
    public int compareTo(@Nonnull OpusPacket o)
    {
        return getSequence() - o.getSequence();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getSequence(), getTimestamp(), getOpusAudio());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof OpusPacket))
            return false;
        OpusPacket other = (OpusPacket) obj;
        return getSequence() == other.getSequence()
            && getTimestamp() == other.getTimestamp()
            && getSSRC() == other.getSSRC();
    }
}
