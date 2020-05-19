package me.syari.ss.discord.internal.requests;

import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class WebSocketSendingThread implements Runnable {
    private final WebSocketClient client;
    private final ReentrantLock queueLock;
    private final Queue<String> chunkSyncQueue;
    private final Queue<String> ratelimitQueue;
    private final ScheduledExecutorService executor;
    private Future<?> handle;
    private boolean needRateLimit = false;
    private boolean attemptedToSend = false;
    private boolean shutdown = false;

    WebSocketSendingThread(@NotNull WebSocketClient client) {
        this.client = client;
        this.queueLock = client.queueLock;
        this.chunkSyncQueue = client.chunkSyncQueue;
        this.ratelimitQueue = client.ratelimitQueue;
        this.executor = client.executor;
    }

    public void shutdown() {
        shutdown = true;
        if (handle != null) handle.cancel(false);
    }

    public void start() {
        shutdown = false;
        handle = executor.submit(this);
    }

    private void scheduleIdle() {
        if (shutdown) return;
        handle = executor.schedule(this, 500, TimeUnit.MILLISECONDS);
    }

    private void scheduleSentMessage() {
        if (shutdown) return;
        handle = executor.schedule(this, 10, TimeUnit.MILLISECONDS);
    }

    private void scheduleRateLimit() {
        if (shutdown) return;
        handle = executor.schedule(this, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        if (!client.sentAuthInfo) {
            scheduleIdle();
            return;
        }
        try {
            attemptedToSend = false;
            needRateLimit = false;
            queueLock.lockInterruptibly();
            String chunkOrSyncRequest = chunkSyncQueue.peek();
            if (chunkOrSyncRequest != null) {
                handleChunkSync(chunkOrSyncRequest);
            } else {
                handleNormalRequest();
            }
            if (needRateLimit) {
                scheduleRateLimit();
            } else if (!attemptedToSend) {
                scheduleIdle();
            } else {
                scheduleSentMessage();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            client.maybeUnlock();
        }
    }

    private void handleChunkSync(String chunkOrSyncRequest) {
        if (send(chunkOrSyncRequest)) chunkSyncQueue.remove();
    }

    private void handleNormalRequest() {
        String message = ratelimitQueue.peek();
        if (message != null && send(message)) ratelimitQueue.remove();
    }

    private boolean send(String request) {
        needRateLimit = !client.send(request, false);
        attemptedToSend = true;
        return !needRateLimit;
    }
}
