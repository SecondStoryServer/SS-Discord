package me.syari.ss.discord.internal.requests.ratelimit;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.utils.data.DataObject;
import me.syari.ss.discord.internal.requests.RateLimiter;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.Route;
import me.syari.ss.discord.internal.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class ClientRateLimiter extends RateLimiter {
    volatile Long globalCooldown = null;

    public ClientRateLimiter(Requester requester) {
        super(requester);
    }

    @Override
    public Long getRateLimit(Route.CompiledRoute route) {
        Bucket bucket = getBucket(route);
        synchronized (bucket) {
            return bucket.getRateLimit();
        }
    }

    @Override
    protected void queueRequest(Request request) {
        Bucket bucket = getBucket(request.getRoute());
        synchronized (bucket) {
            bucket.addToQueue(request);
        }
    }

    @Override
    protected Long handleResponse(Route.CompiledRoute route, okhttp3.Response response) {
        Bucket bucket = getBucket(route);
        synchronized (bucket) {
            long now = System.currentTimeMillis();
            int code = response.code();
            if (code == 429) {
                try (InputStream in = IOUtil.getBody(response)) {
                    DataObject limitObj = DataObject.fromJson(in);
                    long retryAfter = limitObj.getLong("retry_after");

                    if (limitObj.hasKey("global") && limitObj.getBoolean("global"))    //Global ratelimit
                        globalCooldown = now + retryAfter;
                    else
                        bucket.retryAfter = now + retryAfter;

                    return retryAfter;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                return null;
            }
        }
    }

    private Bucket getBucket(Route.CompiledRoute route) {
        String baseRoute = route.getBaseRoute().getRoute();
        Bucket bucket = (Bucket) buckets.get(baseRoute);
        if (bucket == null) {
            synchronized (buckets) {
                bucket = (Bucket) buckets.get(baseRoute);
                if (bucket == null) {
                    bucket = new Bucket(baseRoute);
                    buckets.put(baseRoute, bucket);
                }
            }
        }
        return bucket;
    }

    private class Bucket implements IBucket, Runnable {
        final String route;
        final ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<>();
        volatile long retryAfter = 0;

        public Bucket(String route) {
            this.route = route;
        }

        void addToQueue(Request request) {
            requests.add(request);
            submitForProcessing();
        }

        void submitForProcessing() {
            synchronized (submittedBuckets) {
                if (!submittedBuckets.contains(this)) {
                    Long delay = getRateLimit();
                    if (delay == null)
                        delay = 0L;

                    requester.getJDA().getRateLimitPool().schedule(this, delay, TimeUnit.MILLISECONDS);
                    submittedBuckets.add(this);
                }
            }
        }

        Long getRateLimit() {
            long now = System.currentTimeMillis();
            if (globalCooldown != null) //Are we on global cooldown?
            {
                if (now > globalCooldown)   //Verify that we should still be on cooldown.
                {
                    globalCooldown = null;  //If we are done cooling down, reset the globalCooldown and continue.
                } else {
                    return globalCooldown - now;    //If we should still be on cooldown, return when we can go again.
                }
            }
            if (this.retryAfter > now) {
                return this.retryAfter - now;
            } else {
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Bucket))
                return false;

            Bucket oBucket = (Bucket) o;
            return route.equals(oBucket.route);
        }

        @Override
        public int hashCode() {
            return route.hashCode();
        }

        @Override
        public void run() {
            requester.setContext();
            try {
                synchronized (requests) {
                    for (Iterator<Request> it = requests.iterator(); it.hasNext(); ) {
                        Request request = null;
                        try {
                            request = it.next();
                            if (isSkipped(it, request))
                                continue;
                            // Blocking code because I'm lazy and client accounts are not priority
                            Long retryAfter = requester.execute(request);
                            if (retryAfter != null)
                                break;
                            else
                                it.remove();
                        } catch (Throwable t) {
                            log.error("Error executing REST request", t);
                            it.remove();
                            if (request != null)
                                request.onFailure(t);
                        }
                    }

                    synchronized (submittedBuckets) {
                        submittedBuckets.remove(this);
                        if (!requests.isEmpty()) {
                            try {
                                this.submitForProcessing();
                            } catch (RejectedExecutionException e) {
                                log.debug("Caught RejectedExecutionException when re-queuing a ratelimited request. The requester is probably shutdown, thus, this can be ignored.");
                            }
                        }
                    }
                }
            } catch (Throwable err) {
                log.error("There was some exception in the ClientRateLimiter", err);
            }
        }

        @Override
        public Queue<Request> getRequests() {
            return requests;
        }
    }
}
