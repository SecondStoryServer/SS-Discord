

package me.syari.ss.discord.api.audio;

import club.minnced.opus.util.OpusLibrary;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.IOException;


public final class AudioNatives
{
    private static final Logger LOG = JDALogger.getLog(AudioNatives.class);
    private static boolean initialized;
    private static boolean audioSupported;

    private AudioNatives() {}


    public static boolean isAudioSupported()
    {
        return audioSupported;
    }


    public static boolean isInitialized()
    {
        return initialized;
    }


    public static synchronized boolean ensureOpus()
    {
        if (initialized)
            return audioSupported;
        initialized = true;
        try
        {
            if (OpusLibrary.isInitialized())
                return audioSupported = true;
            audioSupported = OpusLibrary.loadFromJar();
        }
        catch (Throwable e)
        {
            handleException(e);
        }
        finally
        {
            if (audioSupported)
                LOG.info("Audio System successfully setup!");
            else
                LOG.info("Audio System encountered problems while loading, thus, is disabled.");
        }
        return audioSupported;
    }

    private static void handleException(Throwable e)
    {
        if (e instanceof UnsupportedOperationException)
        {
            LOG.error("Sorry, JDA's audio system doesn't support this system.\n{}", e.getMessage());
        }
        else if (e instanceof NoClassDefFoundError)
        {
            LOG.error("Missing opus dependency, unable to initialize audio!");
        }
        else if (e instanceof IOException)
        {
            LOG.error("There was an IO Exception when setting up the temp files for audio.", e);
        }
        else if (e instanceof UnsatisfiedLinkError)
        {
            LOG.error("JDA encountered a problem when attempting to load the Native libraries. Contact a DEV.", e);
        }
        else if (e instanceof Error)
        {
            throw (Error) e;
        }
        else
        {
            LOG.error("An unknown exception occurred while attempting to setup JDA's audio system!", e);
        }
    }
}
