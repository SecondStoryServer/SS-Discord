

package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.annotations.Incubating;
import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.audio.AudioReceiveHandler;
import me.syari.ss.discord.api.audio.AudioSendHandler;
import me.syari.ss.discord.api.audio.SpeakingMode;
import me.syari.ss.discord.api.audio.hooks.ConnectionListener;
import me.syari.ss.discord.api.audio.hooks.ConnectionStatus;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;



public interface AudioManager
{
    long DEFAULT_CONNECTION_TIMEOUT = 10000;
    Logger LOG = JDALogger.getLog(AudioManager.class);


    @Nonnull
    JDA getJDA();


    @Nonnull
    Guild getGuild();


    boolean isAttemptingToConnect();


    @Nullable
    VoiceChannel getQueuedAudioConnection();


    @Nullable
    VoiceChannel getConnectedChannel();


    boolean isConnected();


    long getConnectTimeout();


    void setSendingHandler(@Nullable AudioSendHandler handler);


    @Nullable
    AudioSendHandler getSendingHandler();


    void setReceivingHandler(@Nullable AudioReceiveHandler handler);


    @Nullable
    AudioReceiveHandler getReceivingHandler();


    void setConnectionListener(@Nullable ConnectionListener listener);


    @Nullable
    ConnectionListener getConnectionListener();


    void setAutoReconnect(boolean shouldReconnect);


    boolean isAutoReconnect();


    void setSelfMuted(boolean muted);


    boolean isSelfMuted();


    void setSelfDeafened(boolean deafened);


    boolean isSelfDeafened();
}
