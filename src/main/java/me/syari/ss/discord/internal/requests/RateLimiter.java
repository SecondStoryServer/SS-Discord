package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.concurrent.CancellationException;

public abstract class RateLimiter {
    protected static final Logger log = JDALogger.getLog(RateLimiter.class);
    protected final Requester requester;
    protected volatile boolean isShutdown = false;

    protected RateLimiter(Requester requester) {
        this.requester = requester;
    }

    protected boolean isSkipped(Iterator<Request> it, Request request) {
        try {
            if (request.isCanceled()) {
                cancel(it, request, new CancellationException("RestAction has been cancelled"));
                return true;
            }
        } catch (Throwable exception) {
            cancel(it, request, exception);
            return true;
        }
        return false;
    }

    private void cancel(@NotNull Iterator<Request> iterator, @NotNull Request request, Throwable exception) {
        request.onFailure(exception);
        iterator.remove();
    }

    // -- Required Implementations --
    public abstract Long getRateLimit(Route.CompiledRoute route);

    protected abstract void queueRequest(Request request);

    protected abstract Long handleResponse(Route.CompiledRoute route, okhttp3.Response response);


    // --- Default Implementations --

    public void init() {
    }

    protected void shutdown() {
        isShutdown = true;
    }
}
