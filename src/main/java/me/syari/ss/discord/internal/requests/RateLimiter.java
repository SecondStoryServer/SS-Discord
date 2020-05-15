

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.internal.utils.JDALogger;
import me.syari.ss.discord.internal.requests.ratelimit.IBucket;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class RateLimiter
{
    //Implementations of this class exist in the me.syari.ss.discord.api.requests.ratelimit package.
    protected static final Logger log = JDALogger.getLog(RateLimiter.class);
    protected final Requester requester;
    protected volatile boolean isShutdown = false;
    protected final ConcurrentHashMap<String, IBucket> buckets = new ConcurrentHashMap<>();
    protected final ConcurrentLinkedQueue<IBucket> submittedBuckets = new ConcurrentLinkedQueue<>();

    protected RateLimiter(Requester requester)
    {
        this.requester = requester;
    }

    protected boolean isSkipped(Iterator<Request> it, Request request)
    {
        try
        {
            if (request.isCanceled() || !request.runChecks())
            {
                cancel(it, request, new CancellationException("RestAction has been cancelled"));
                return true;
            }
        }
        catch (Throwable exception)
        {
            cancel(it, request, exception);
            return true;
        }
        return false;
    }

    private void cancel(Iterator<Request> it, Request request, Throwable exception)
    {
        request.onFailure(exception);
        it.remove();
    }

    // -- Required Implementations --
    public abstract Long getRateLimit(Route.CompiledRoute route);
    protected abstract void queueRequest(Request request);
    protected abstract Long handleResponse(Route.CompiledRoute route, okhttp3.Response response);


    // --- Default Implementations --

    public boolean isRateLimited(Route.CompiledRoute route)
    {
        return getRateLimit(route) != null;
    }

    public List<IBucket> getRouteBuckets()
    {
        synchronized (buckets)
        {
            return Collections.unmodifiableList(new ArrayList<>(buckets.values()));
        }
    }

    public List<IBucket> getQueuedRouteBuckets()
    {
        synchronized (submittedBuckets)
        {
            return Collections.unmodifiableList(new ArrayList<>(submittedBuckets));
        }
    }

    public void init() {}

    protected void shutdown()
    {
        isShutdown = true;

//        pool.setKeepAliveTime(time, unit);
//        pool.allowCoreThreadTimeOut(true);
    }
}
