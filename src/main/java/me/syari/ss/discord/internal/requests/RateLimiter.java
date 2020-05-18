package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.api.requests.Request;
import me.syari.ss.discord.api.utils.MiscUtil;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {
    private static final String RESET_AFTER_HEADER = "X-RateLimit-Reset-After";
    private static final String LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    private static final String GLOBAL_HEADER = "X-RateLimit-Global";
    private static final String HASH_HEADER = "X-RateLimit-Bucket";
    private static final String RETRY_AFTER_HEADER = "Retry-After";
    private static final String UNLIMITED_BUCKET = "unlimited";
    protected final Requester requester;
    private final ReentrantLock bucketLock = new ReentrantLock();
    private final Map<Route, String> hash = new ConcurrentHashMap<>();
    private final Map<String, Bucket> bucket = new ConcurrentHashMap<>();
    private final Map<Bucket, Future<?>> rateLimitQueue = new ConcurrentHashMap<>();
    private Future<?> cleanupWorker;

    public RateLimiter(Requester requester) {
        this.requester = requester;
    }

    public void init() {
        cleanupWorker = getScheduler().scheduleAtFixedRate(this::cleanup, 30, 30, TimeUnit.SECONDS);
    }

    private @NotNull ScheduledExecutorService getScheduler() {
        return requester.getJDA().getRateLimitPool();
    }

    private void cleanup() {
        MiscUtil.locked(bucketLock, () -> {
            int size = bucket.size();
            Iterator<Map.Entry<String, Bucket>> entries = bucket.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<String, Bucket> entry = entries.next();
                Bucket bucket = entry.getValue();
                if (bucket.isUnlimited() && bucket.requests.isEmpty()) {
                    entries.remove();
                } else if (bucket.requests.isEmpty() && bucket.reset <= getNow()) {
                    entries.remove();
                }
            }
        });
    }

    private String getRouteHash(Route route) {
        return hash.getOrDefault(route, UNLIMITED_BUCKET + "+" + route);
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

    public void shutdown() {
        if (cleanupWorker != null) {
            cleanupWorker.cancel(false);
        }
    }

    public Long getRateLimit(Route.CompiledRoute route) {
        Bucket bucket = getBucket(route, false);
        return bucket == null ? 0L : bucket.getRateLimit();
    }

    @SuppressWarnings("rawtypes")
    public void queueRequest(Request request) {
        MiscUtil.locked(bucketLock, () -> {
            Bucket bucket = getBucket(request.getRoute(), true);
            bucket.enqueue(request);
            runBucket(bucket);
        });
    }

    public Long handleResponse(Route.CompiledRoute route, okhttp3.Response response) {
        bucketLock.lock();
        try {
            long rateLimit = updateBucket(route, response).getRateLimit();
            if (response.code() == 429) {
                return rateLimit;
            } else {
                return null;
            }
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

                Route baseRoute = route.getBaseRoute();
                if (hash != null) {
                    if (!this.hash.containsKey(baseRoute)) {
                        this.hash.put(baseRoute, hash);
                    }

                    bucket = getBucket(route, true);
                }

                if (global) {
                    String retryAfterHeader = headers.get(RETRY_AFTER_HEADER);
                    long retryAfter = parseLong(retryAfterHeader);
                    requester.getJDA().getSessionController().setGlobalRatelimit(now + retryAfter);
                } else if (response.code() == 429) {
                    String retryAfterHeader = headers.get(RETRY_AFTER_HEADER);
                    long retryAfter = parseLong(retryAfterHeader);
                    bucket.remaining = 0;
                    bucket.reset = getNow() + retryAfter;
                    return bucket;
                }

                if (hash == null) {
                    return bucket;
                }

                String limitHeader = headers.get(LIMIT_HEADER);
                String remainingHeader = headers.get(REMAINING_HEADER);
                String resetAfterHeader = headers.get(RESET_AFTER_HEADER);

                bucket.limit = (int) Math.max(1L, parseLong(limitHeader));
                bucket.remaining = (int) parseLong(remainingHeader);
                bucket.reset = now + parseDouble(resetAfterHeader);
                return bucket;
            } catch (Exception e) {
                return getBucket(route, true);
            }
        });
    }

    private Bucket getBucket(Route.CompiledRoute route, boolean create) {
        return MiscUtil.locked(bucketLock, () ->
        {
            String hash = getRouteHash(route.getBaseRoute());
            String bucketId = hash + ":" + route.getMajorParameters();
            Bucket bucket = this.bucket.get(bucketId);
            if (bucket == null && create) {
                this.bucket.put(bucketId, bucket = new Bucket(bucketId));
            }

            return bucket;
        });
    }

    private void runBucket(Bucket bucket) {
        MiscUtil.locked(bucketLock, () ->
                rateLimitQueue.computeIfAbsent(bucket,
                        (k) -> getScheduler().schedule(bucket, bucket.getRateLimit(), TimeUnit.MILLISECONDS)));
    }

    private long parseLong(String input) {
        return input == null ? 0L : Long.parseLong(input);
    }

    private long parseDouble(String input) {
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
            if (now < global) {
                return global - now;
            }
            if (reset <= now) {
                remaining = limit;
                return 0L;
            }

            return remaining < 1 ? reset - now : 0L;
        }

        private boolean isUnlimited() {
            return bucketId.startsWith("unlimited");
        }

        private void backoff() {
            MiscUtil.locked(bucketLock, () -> {
                rateLimitQueue.remove(this);
                if (!requests.isEmpty()) {
                    runBucket(this);
                }
            });
        }

        @Override
        public void run() {
            Iterator<Request> iterator = requests.iterator();
            while (iterator.hasNext()) {
                Long rateLimit = getRateLimit();
                if (0L < rateLimit) {
                    break;
                }

                Request request = iterator.next();
                if (isUnlimited()) {
                    boolean shouldSkip = MiscUtil.locked(bucketLock, () -> {
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

                if (isSkipped(iterator, request)) {
                    continue;
                }

                try {
                    rateLimit = requester.execute(request, false);
                    if (rateLimit == null) {
                        iterator.remove();
                    } else {
                        break;
                    }
                } catch (Exception ex) {
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
