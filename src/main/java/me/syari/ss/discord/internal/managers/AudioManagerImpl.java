
package me.syari.ss.discord.internal.managers;

import me.syari.ss.discord.api.entities.Member;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.internal.JDAImpl;
import me.syari.ss.discord.internal.audio.AudioConnection;
import me.syari.ss.discord.internal.entities.GuildImpl;
import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.PermissionUtil;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.audio.AudioReceiveHandler;
import me.syari.ss.discord.api.audio.AudioSendHandler;
import me.syari.ss.discord.api.audio.SpeakingMode;
import me.syari.ss.discord.api.audio.hooks.ConnectionListener;
import me.syari.ss.discord.api.audio.hooks.ConnectionStatus;
import me.syari.ss.discord.api.audio.hooks.ListenerProxy;
import me.syari.ss.discord.api.entities.VoiceChannel;
import me.syari.ss.discord.api.managers.AudioManager;
import me.syari.ss.discord.api.utils.MiscUtil;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;

public class AudioManagerImpl implements AudioManager
{
    public final ReentrantLock CONNECTION_LOCK = new ReentrantLock();

    protected final ListenerProxy connectionListener = new ListenerProxy();
    protected final GuildImpl guild;
    protected VoiceChannel queuedAudioConnection = null;
    protected AudioConnection audioConnection = null;
    protected EnumSet<SpeakingMode> speakingModes = EnumSet.of(SpeakingMode.VOICE);

    protected AudioSendHandler sendHandler;
    protected AudioReceiveHandler receiveHandler;
    protected long queueTimeout = 100;
    protected boolean shouldReconnect = true;

    protected boolean selfMuted = false;
    protected boolean selfDeafened = false;

    protected long timeout = DEFAULT_CONNECTION_TIMEOUT;
    protected int speakingDelay = 0;

    public AudioManagerImpl(GuildImpl guild)
    {
        this.guild = guild;
    }

    public AudioConnection getAudioConnection()
    {
        return audioConnection;
    }

    @Override
    public void openAudioConnection(VoiceChannel channel)
    {
        Checks.notNull(channel, "Provided VoiceChannel");

//        if (!AUDIO_SUPPORTED)
//            throw new UnsupportedOperationException("Sorry! Audio is disabled due to an internal JDA error! Contact Dev!");
        if (!getGuild().equals(channel.getGuild()))
            throw new IllegalArgumentException("The provided VoiceChannel is not a part of the Guild that this AudioManager handles." +
                    "Please provide a VoiceChannel from the proper Guild");
        final Member self = getGuild().getSelfMember();
        //if (!self.hasPermission(channel, Permission.VOICE_CONNECT))
        //    throw new InsufficientPermissionException(Permission.VOICE_CONNECT);

        if (audioConnection == null)
        {
            checkChannel(channel, self);
            //Start establishing connection, joining provided channel
            queuedAudioConnection = channel;
            getJDA().getDirectAudioController().connect(channel);
        }
        else
        {
            //Connection is already established, move to specified channel

            //If we are already connected to this VoiceChannel, then do nothing.
            if (channel.equals(audioConnection.getChannel()))
                return;

            checkChannel(channel, self);

            getJDA().getDirectAudioController().connect(channel);
            audioConnection.setChannel(channel);
        }
    }

    private void checkChannel(VoiceChannel channel, Member self)
    {
        EnumSet<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(channel, self));
        if (!perms.contains(Permission.VOICE_CONNECT))
            throw new InsufficientPermissionException(channel, Permission.VOICE_CONNECT);
        final int userLimit = channel.getUserLimit(); // userLimit is 0 if no limit is set!
        if (userLimit > 0 && !perms.contains(Permission.ADMINISTRATOR))
        {
            // Check if we can actually join this channel
            // - If there is a userlimit
            // - If that userlimit is reached
            // - If we don't have voice move others permissions
            // VOICE_MOVE_OTHERS allows access because you would be able to move people out to
            // open up a slot anyway
            if (userLimit <= channel.getMembers().size()
                && !perms.contains(Permission.VOICE_MOVE_OTHERS))
            {
                throw new InsufficientPermissionException(channel, Permission.VOICE_MOVE_OTHERS,
                    "Unable to connect to VoiceChannel due to userlimit! Requires permission VOICE_MOVE_OTHERS to bypass");
            }
        }
    }

    @Override
    public void closeAudioConnection()
    {
        getJDA().getAudioLifeCyclePool().execute(() -> {
            getJDA().setContext();
            closeAudioConnection(ConnectionStatus.NOT_CONNECTED);
        });
    }

    public void closeAudioConnection(ConnectionStatus reason)
    {
        MiscUtil.locked(CONNECTION_LOCK, () ->
        {
            this.queuedAudioConnection = null;
            if (audioConnection != null)
                this.audioConnection.close(reason);
            else if (reason != ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD)
                getJDA().getDirectAudioController().disconnect(getGuild());
            this.audioConnection = null;
        });
    }

    @Override
    public void setSpeakingMode(@Nonnull Collection<SpeakingMode> mode)
    {
        Checks.notEmpty(mode, "Speaking Mode");
        this.speakingModes = EnumSet.copyOf(mode);
        if (audioConnection != null)
            audioConnection.setSpeakingMode(this.speakingModes);
    }

    @Nonnull
    @Override
    public EnumSet<SpeakingMode> getSpeakingMode()
    {
        return EnumSet.copyOf(this.speakingModes);
    }

    @Override
    public void setSpeakingDelay(int millis)
    {
        this.speakingDelay = millis;
        if (audioConnection != null)
            audioConnection.setSpeakingDelay(millis);
    }

    @Nonnull
    @Override
    public JDAImpl getJDA()
    {
        return getGuild().getJDA();
    }

    @Nonnull
    @Override
    public GuildImpl getGuild()
    {
        return guild;
    }

    @Override
    public boolean isAttemptingToConnect()
    {
        return queuedAudioConnection != null;
    }

    @Override
    public VoiceChannel getQueuedAudioConnection()
    {
        return queuedAudioConnection;
    }

    @Override
    public VoiceChannel getConnectedChannel()
    {
        return audioConnection == null ? null : audioConnection.getChannel();
    }

    @Override
    public boolean isConnected()
    {
        return audioConnection != null;
    }

    @Override
    public void setConnectTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    @Override
    public long getConnectTimeout()
    {
        return timeout;
    }

    @Override
    public void setSendingHandler(AudioSendHandler handler)
    {
        sendHandler = handler;
        if (audioConnection != null)
            audioConnection.setSendingHandler(handler);
    }

    @Override
    public AudioSendHandler getSendingHandler()
    {
        return sendHandler;
    }

    @Override
    public void setReceivingHandler(AudioReceiveHandler handler)
    {
        receiveHandler = handler;
        if (audioConnection != null)
            audioConnection.setReceivingHandler(handler);
    }

    @Override
    public AudioReceiveHandler getReceivingHandler()
    {
        return receiveHandler;
    }

    @Override
    public void setConnectionListener(ConnectionListener listener)
    {
        this.connectionListener.setListener(listener);
    }

    @Override
    public ConnectionListener getConnectionListener()
    {
        return connectionListener.getListener();
    }

    @Nonnull
    @Override
    public ConnectionStatus getConnectionStatus()
    {
        if (audioConnection != null)
            return audioConnection.getConnectionStatus();
        else
            return ConnectionStatus.NOT_CONNECTED;
    }

    @Override
    public void setAutoReconnect(boolean shouldReconnect)
    {
        this.shouldReconnect = shouldReconnect;
        if (audioConnection != null)
            audioConnection.setAutoReconnect(shouldReconnect);
    }

    @Override
    public boolean isAutoReconnect()
    {
        return shouldReconnect;
    }

    @Override
    public void setSelfMuted(boolean muted)
    {
        if (selfMuted != muted)
        {
            this.selfMuted = muted;
            updateVoiceState();
        }
    }

    @Override
    public boolean isSelfMuted()
    {
        return selfMuted;
    }

    @Override
    public void setSelfDeafened(boolean deafened)
    {
        if (selfDeafened != deafened)
        {
            this.selfDeafened = deafened;
            updateVoiceState();
        }

    }

    @Override
    public boolean isSelfDeafened()
    {
        return selfDeafened;
    }

    public ConnectionListener getListenerProxy()
    {
        return connectionListener;
    }

    public void setAudioConnection(AudioConnection audioConnection)
    {
        this.audioConnection = audioConnection;
        if (audioConnection == null)
            return;

        this.queuedAudioConnection = null;
        audioConnection.setSendingHandler(sendHandler);
        audioConnection.setReceivingHandler(receiveHandler);
        audioConnection.setQueueTimeout(queueTimeout);
        audioConnection.setSpeakingMode(speakingModes);
        audioConnection.setSpeakingDelay(speakingDelay);
    }

    public void prepareForRegionChange()
    {
        VoiceChannel queuedChannel = audioConnection.getChannel();
        closeAudioConnection(ConnectionStatus.AUDIO_REGION_CHANGE);
        this.queuedAudioConnection = queuedChannel;
    }

    public void setQueuedAudioConnection(VoiceChannel channel)
    {
        queuedAudioConnection = channel;
    }

    public void setConnectedChannel(VoiceChannel channel)
    {
        if (audioConnection != null)
            audioConnection.setChannel(channel);
    }

    public void setQueueTimeout(long queueTimeout)
    {
        this.queueTimeout = queueTimeout;
        if (audioConnection != null)
            audioConnection.setQueueTimeout(queueTimeout);
    }

    protected void updateVoiceState()
    {
        if (isConnected() || isAttemptingToConnect())
        {
            VoiceChannel channel = isConnected() ? getConnectedChannel() : getQueuedAudioConnection();

            //This is technically equivalent to an audio open/move packet.
            getJDA().getDirectAudioController().connect(channel);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize()
    {
        if (audioConnection != null)
        {
            LOG.warn("Finalized AudioManager with active audio connection. GuildId: {}", getGuild().getId());
            audioConnection.close(ConnectionStatus.DISCONNECTED_REMOVED_FROM_GUILD);
        }
        audioConnection = null;
    }
}
