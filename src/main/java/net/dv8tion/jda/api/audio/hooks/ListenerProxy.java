

package net.dv8tion.jda.api.audio.hooks;

import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class ListenerProxy implements ConnectionListener
{
    private static final Logger log = LoggerFactory.getLogger(ListenerProxy.class);
    private volatile ConnectionListener listener = null;

    @Override
    public void onPing(long ping)
    {
        if (listener == null)
            return;
        ConnectionListener listener = this.listener;
        try
        {
            if (listener != null)
                listener.onPing(ping);
        }
        catch (Throwable t)
        {
            log.error("The ConnectionListener encountered and uncaught exception", t);
        }
    }

    @Override
    public void onStatusChange(@Nonnull ConnectionStatus status)
    {
        if (listener == null)
            return;
        ConnectionListener listener = this.listener;
        try
        {
            if (listener != null)
                listener.onStatusChange(status);
        }
        catch (Throwable t)
        {
            log.error("The ConnectionListener encountered and uncaught exception", t);
        }
    }

    @Override
    public void onUserSpeaking(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes)
    {
        if (listener == null)
            return;
        ConnectionListener listener = this.listener;
        try
        {
            if (listener != null)
            {
                listener.onUserSpeaking(user, modes);
                listener.onUserSpeaking(user, modes.contains(SpeakingMode.VOICE));
                listener.onUserSpeaking(user, modes.contains(SpeakingMode.VOICE), modes.contains(SpeakingMode.SOUNDSHARE));
            }
        }
        catch (Throwable t)
        {
            log.error("The ConnectionListener encountered and uncaught exception", t);
        }
    }

    @Override
    public void onUserSpeaking(@Nonnull User user, boolean speaking) {}

    public void setListener(ConnectionListener listener)
    {
        this.listener = listener;
    }

    public ConnectionListener getListener()
    {
        return listener;
    }
}
