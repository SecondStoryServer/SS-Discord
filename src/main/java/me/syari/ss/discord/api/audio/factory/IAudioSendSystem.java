

package me.syari.ss.discord.api.audio.factory;

import javax.annotation.CheckForNull;
import java.util.concurrent.ConcurrentMap;


public interface IAudioSendSystem
{
    
    void start();

    
    void shutdown();

    
    default void setContextMap(@CheckForNull ConcurrentMap<String, String> contextMap) {}
}
