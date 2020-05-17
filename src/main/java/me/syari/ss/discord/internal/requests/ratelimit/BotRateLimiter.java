package me.syari.ss.discord.internal.requests.ratelimit;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.utils.MiscUtil;
import me.syari.ss.discord.internal.requests.RateLimiter;
import me.syari.ss.discord.internal.requests.Requester;
import me.syari.ss.discord.internal.requests.Route;
import okhttp3.Headers;
import org.jetbrains.annotations.Contract;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class BotRateLimiter extends RateLimiter {
    private static final String RESET_AFTER_HEADER = "X-RateLimit-Reset-After";
    private static final String RESET_HEADER = "X-RateLimit-Reset";
    private static final String LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String GLOBAL_HEADER = "X-RateLimit-Global";
    private static final String HASH_HEADER = "X-RateLimit-Bucket";
    private static final String RETRY_AFTER_HEADER = "Retry-After";
    private static final String UNLIMITED_BUCKET = "unlimited";

    private final ReentrantLock bucketLock = new ReentrantLock();
    private final Map<Route, String> hash = new ConcurrentHashMap<>();
    private final Map<String, Bucket> bucket = new ConcurrentHashMap<>();
    private final Map<Bucket, Future<?>> rateLimitQueue = new ConcurrentHashMap<>();
    private Future<?> cleanupWorker;

    public BotRateLimiter(Requester requester) {
        super(requester);
    }

    @Override
    public void init() {
        cleanupWorker = getScheduler().scheduleAtFixedRate(this::cleanup, 30, 30, TimeUnit.SECONDS);
    }

    private ScheduledExecutorService getScheduler() {
        return requester.getJDA().getRateLimitPool();
    }

    private void cleanup() {
        MiscUtil.locked(bucketLock, () -> {
            int size = bucket.size();
            Iterator<Map.Entry<String, Bucket>> entries = bucket.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<String, Bucket> entry = entries.next();
                Bucket bucket = entry.getValue();
                if (bucket.isUnlimited() && bucket.requests.isEmpty())
                    entries.remove();
                else if (bucket.requests.isEmpty() && bucket.reset <= getNow())
                    entries.remove();
            }
            size -= bucket.size();
            if (size > 0)
                log.debug("Removed {} expired buckets", size);
        });
    }

    private String getRouteHash(Route route) {
        return hash.getOrDefault(route, UNLIMITED_BUCKET + "+" + route);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        if (cleanupWorker != null)
            cleanupWorker.cancel(false);
    }

    @Override
    public Long getRateLimit(Route.CompiledRoute route) {
        Bucket bucket = getBucket(route, false);
        return bucket == null ? 0L : bucket.getRateLimit();
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void queueRequest(Request request) {
        // Create bucket and enqueue request
        MiscUtil.locked(bucketLock, () -> {
            Bucket bucket = getBucket(request.getRoute(), true);
            bucket.enqueue(request);
            runBucket(bucket);
        });
    }

    @Override
    protected Long handleResponse(Route.CompiledRoute route, okhttp3.Response response) {
        bucketLock.lock();
        try {
            long rateLimit = updateBucket(route, response).getRateLimit();
            if (response.code() == 429)
                return rateLimit;
            else
                return null;
        } finally {
            bucketLock.unlock();
        }
    }

    private Bucket updateBucket(Route.CompiledRoute route, okhttp3.Response response) {
        return MiscUtil.locked(bucketLock, () -> {
            try {
                Bucket bucket = getBucket(route, true);
                Headers headers = response.headers();

                boolean wasUnlimited = bucket.isUnlimited();
                boolean global = headers.get(GLOBAL_HEADER) != null;
                String hash = headers.get(HASH_HEADER);
                long now = getNow();

                // Create a new bucket for the hash if needed
                Route baseRoute = route.getBaseRoute();
                if (hash != null) {
                    if (!this.hash.containsKey(baseRoute)) {
                        this.hash.put(baseRoute, hash);
                        log.debug("Caching bucket hash {} -> {}", baseRoute, hash);
                    }

                    bucket = getBucket(route, true);
                }

                // Handle global rate limit if necessary
                if (global) {
                    String retryAfterHeader = headers.get(RETRY_AFTER_HEADER);
                    long retryAfter = parseLong(retryAfterHeader);
                    requester.getJDA().getSessionController().setGlobalRatelimit(now + retryAfter);
                    log.error("Encountered global rate limit! Retry-After: {} ms", retryAfter);
                }
                // Handle hard rate limit, pretty much just log that it happened
                else if (response.code() == 429) {
                    // Update the bucket to the new information
                    String retryAfterHeader = headers.get(RETRY_AFTER_HEADER);
                    long retryAfter = parseLong(retryAfterHeader);
                    bucket.remaining = 0;
                    bucket.reset = getNow() + retryAfter;
                    // don't log warning if we are switching bucket, this means it was an issue with an un-hashed route that is now resolved
                    if (hash == null || !wasUnlimited)
                        log.warn("Encountered 429 on route {} with bucket {} Retry-After: {} ms", baseRoute, bucket.bucketId, retryAfter);
                    else
                        log.debug("Encountered 429 on route {} with bucket {} Retry-After: {} ms", baseRoute, bucket.bucketId, retryAfter);
                    return bucket;
                }

                // If hash is null this means we didn't get enough information to update a bucket
                if (hash == null)
                    return bucket;

                // Update the bucket parameters with new information
                String limitHeader = headers.get(LIMIT_HEADER);
                String remainingHeader = headers.get(REMAINING_HEADER);
                String resetAfterHeader = headers.get(RESET_AFTER_HEADER);
                String resetHeader = headers.get(RESET_HEADER);

                bucket.limit = (int) Math.max(1L, parseLong(limitHeader));
                bucket.remaining = (int) parseLong(remainingHeader);
                if (requester.getJDA().isRelativeRateLimit())
                    bucket.reset = now + parseDouble(resetAfterHeader);
                else
                    bucket.reset = parseDouble(resetHeader);
                log.trace("Updated bucket {} to ({}/{}, {})", bucket.bucketId, bucket.remaining, bucket.limit, bucket.reset - now);
                return bucket;
            } catch (Exception e) {
                Bucket bucket = getBucket(route, true);
                log.error("Encountered Exception while updating a bucket. Route: {} Bucket: {} Code: {} Headers:\n{}",
                        route.getBaseRoute(), bucket, response.code(), response.headers(), e);
                return bucket;
            }
        });
    }

    @Contract("_,true->!null")
    private Bucket getBucket(Route.CompiledRoute route, boolean create) {
        return MiscUtil.locked(bucketLock, () ->
        {
            // Retrieve the hash via the route
            String hash = getRouteHash(route.getBaseRoute());
            // Get or create a bucket for the hash + major parameters
            String bucketId = hash + ":" + route.getMajorParameters();
            Bucket bucket = this.bucket.get(bucketId);
            if (bucket == null && create)
                this.bucket.put(bucketId, bucket = new Bucket(bucketId));

            return bucket;
        });
    }

    private void runBucket(Bucket bucket) {
        if (isShutdown)
            return;
        // Schedule a new bucket worker if no worker is running
        MiscUtil.locked(bucketLock, () ->
                rateLimitQueue.computeIfAbsent(bucket,
                        (k) -> getScheduler().schedule(bucket, bucket.getRateLimit(), TimeUnit.MILLISECONDS)));
    }

    private long parseLong(String input) {
        return input == null ? 0L : Long.parseLong(input);
    }

    private long parseDouble(String input) {
        //The header value is using a double to represent milliseconds and seconds:
        // 5.250 this is 5 seconds and 250 milliseconds (5250 milliseconds)
        return input == null ? 0L : (long) (Double.parseDouble(input) * 1000);
    }

    public long getNow() {
        return System.currentTimeMillis();
    }

    @SuppressWarnings("rawtypes")
    private class Bucket implements Runnable {
        private final String bucketId;
        private final Queue<Request> requests = new ConcurrentLinkedQueue<>();

        private long reset = 0;
        private int remaining = 1;
        private int limit = 1;

        public Bucket(String bucketId) {
            this.bucketId = bucketId;
        }

        public void enqueue(Request request) {
            requests.add(request);
        }

        public long getRateLimit() {
            long now = getNow();
            long global = requester.getJDA().getSessionController().getGlobalRatelimit();
            // Global rate limit is more important to handle
            if (global > now)
                return global - now;
            // Check if the bucket reset time has expired
            if (reset <= now) {
                // Update the remaining uses to the limit (we don't know better)
                remaining = limit;
                return 0L;
            }

            // If there are remaining requests we don't need to do anything, otherwise return backoff in milliseconds
            return remaining < 1 ? reset - now : 0L;
        }

        private boolean isUnlimited() {
            return bucketId.startsWith("unlimited");
        }

        private void backoff() {
            // Schedule backoff if requests are not done
            MiscUtil.locked(bucketLock, () -> {
                rateLimitQueue.remove(this);
                if (!requests.isEmpty())
                    runBucket(this);
            });
        }

        @Override
        public void run() {
            log.trace("Bucket {} is running {} requests", bucketId, requests.size());
            Iterator<Request> iterator = requests.iterator();
            while (iterator.hasNext()) {
                Long rateLimit = getRateLimit();
                if (rateLimit > 0L) {
                    // We need to backoff since we ran out of remaining uses or hit the global rate limit
                    log.debug("Backing off {} ms for bucket {}", rateLimit, bucketId);
                    break;
                }

                Request request = iterator.next();
                if (isUnlimited()) {
                    boolean shouldSkip = MiscUtil.locked(bucketLock, () -> {
                        // Attempt moving request to correct bucket if it has been created
                        Bucket bucket = getBucket(request.getRoute(), true);
                        if (bucket != this) {
                            bucket.enqueue(request);
                            iterator.remove();
                            runBucket(bucket);
                            return true;
                        }
                        return false;
                    });
                    if (shouldSkip) continue;
                }

                if (isSkipped(iterator, request))
                    continue;

                try {
                    rateLimit = requester.execute(request);
                    if (rateLimit != null)
                        break; // this means we hit a hard rate limit (429) so the request needs to be retried

                    // The request went through so we can remove it
                    iterator.remove();
                } catch (Exception ex) {
                    log.error("Encountered exception trying to execute request", ex);
                    break;
                }
            }

            backoff();
        }

        @Override
        public String toString() {
            return bucketId;
        }
    }
}
