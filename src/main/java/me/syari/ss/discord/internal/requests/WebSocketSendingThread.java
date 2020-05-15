

package me.syari.ss.discord.internal.requests;

import gnu.trove.map.TLongObjectMap;
import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.api.entities.GuildVoiceState;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.JDAImpl;


import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

//Helper class delegated to WebSocketClient
class WebSocketSendingThread implements Runnable
{
    private static final Logger LOG = WebSocketClient.LOG;

    private final WebSocketClient client;
    private final JDAImpl api;
    private final ReentrantLock queueLock;
    private final Queue<String> chunkSyncQueue;
    private final Queue<String> ratelimitQueue;
    private final ScheduledExecutorService executor;
    private Future<?> handle;

    private boolean needRateLimit = false;
    private boolean attemptedToSend = false;
    private boolean shutdown = false;

    WebSocketSendingThread(WebSocketClient client)
    {
        this.client = client;
        this.api = client.api;
        this.queueLock = client.queueLock;
        this.chunkSyncQueue = client.chunkSyncQueue;
        this.ratelimitQueue = client.ratelimitQueue;
        this.executor = client.executor;
    }

    public void shutdown()
    {
        shutdown = true;
        if (handle != null)
            handle.cancel(false);
    }

    public void start()
    {
        shutdown = false;
        handle = executor.submit(this);
    }

    private void scheduleIdle()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 500, TimeUnit.MILLISECONDS);
    }

    private void scheduleSentMessage()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 10, TimeUnit.MILLISECONDS);
    }

    private void scheduleRateLimit()
    {
        if (shutdown)
            return;
        handle = executor.schedule(this, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run()
    {
        //Make sure that we don't send any packets before sending auth info.
        if (!client.sentAuthInfo)
        {
            scheduleIdle();
            return;
        }

        try
        {
            api.setContext();
            attemptedToSend = false;
            needRateLimit = false;
            queueLock.lockInterruptibly();

            String chunkOrSyncRequest = chunkSyncQueue.peek();
            if (chunkOrSyncRequest != null)
                handleChunkSync(chunkOrSyncRequest);
            else
                handleNormalRequest();

            if (needRateLimit)
                scheduleRateLimit();
            else if (!attemptedToSend)
                scheduleIdle();
            else
                scheduleSentMessage();
        }
        catch (InterruptedException ignored)
        {
            LOG.debug("Main WS send thread interrupted. Most likely JDA is disconnecting the websocket.");
        }
        finally
        {
            // on any exception that might cause this lock to not release
            client.maybeUnlock();
        }
    }

    private void handleChunkSync(String chunkOrSyncRequest)
    {
        LOG.debug("Sending chunk/sync request {}", chunkOrSyncRequest);
        if (send(chunkOrSyncRequest))
            chunkSyncQueue.remove();
    }

    private void handleNormalRequest()
    {
        String message = ratelimitQueue.peek();
        if (message != null)
        {
            LOG.debug("Sending normal message {}", message);
            if (send(message))
                ratelimitQueue.remove();
        }
    }

    //returns true if send was successful
    private boolean send(String request)
    {
        needRateLimit = !client.send(request, false);
        attemptedToSend = true;
        return !needRateLimit;
    }
}
