package me.syari.ss.discord.internal.requests

import me.syari.ss.discord.api.SessionController
import me.syari.ss.discord.api.requests.Request
import me.syari.ss.discord.internal.utils.ThreadingConfig
import okhttp3.Response
import java.util.Queue
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier

object RateLimiter {
    private const val RESET_AFTER_HEADER = "X-RateLimit-Reset-After"
    private const val LIMIT_HEADER = "X-RateLimit-Limit"
    private const val REMAINING_HEADER = "X-RateLimit-Remaining"
    private const val GLOBAL_HEADER = "X-RateLimit-Global"
    private const val HASH_HEADER = "X-RateLimit-Bucket"
    private const val RETRY_AFTER_HEADER = "Retry-After"
    private fun <E> locked(lock: ReentrantLock, task: Supplier<E>): E {
        return try {
            lock.lockInterruptibly()
            task.get()
        } catch (ex: InterruptedException) {
            throw IllegalStateException(ex)
        } finally {
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }

    private fun locked(lock: ReentrantLock, task: Runnable) {
        try {
            lock.lockInterruptibly()
            task.run()
        } catch (e: InterruptedException) {
            throw IllegalStateException(e)
        } finally {
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }

    private val bucketLock = ReentrantLock()
    private val bucket: MutableMap<String, Bucket> = ConcurrentHashMap()
    private val rateLimitQueue: MutableMap<Bucket?, Future<*>> = ConcurrentHashMap()
    private lateinit var cleanupWorker: Future<*>

    fun init() {
        cleanupWorker = scheduler.scheduleAtFixedRate({ cleanup() }, 30, 30, TimeUnit.SECONDS)
    }

    private val scheduler: ScheduledExecutorService
        get() = ThreadingConfig.rateLimitPool

    private fun cleanup() {
        locked(bucketLock, Runnable {
            val entries: MutableIterator<Map.Entry<String, Bucket>> = bucket.entries.iterator()
            while (entries.hasNext()) {
                val entry = entries.next()
                val bucket = entry.value
                if (bucket.isUnlimited && bucket.requests.isEmpty() || bucket.requests.isEmpty() && bucket.reset <= System.currentTimeMillis()) {
                    entries.remove()
                }
            }
        })
    }

    private fun isSkipped(
        it: MutableIterator<Request<*>>, request: Request<*>
    ): Boolean {
        try {
            if (request.isCanceled) {
                cancel(it, request, CancellationException("RestAction has been cancelled"))
                return true
            }
        } catch (ex: Throwable) {
            cancel(it, request, ex)
            return true
        }
        return false
    }

    private fun cancel(
        iterator: MutableIterator<Request<*>>, request: Request<*>, exception: Throwable
    ) {
        request.onFailure(exception)
        iterator.remove()
    }

    fun shutdown() {
        cleanupWorker.cancel(false)
    }

    fun getRateLimit(route: Route): Long {
        return getBucket(route)?.rateLimit ?: 0L
    }

    fun queueRequest(request: Request<*>) {
        locked(bucketLock, Runnable {
            val bucket = getBucketOrCreate(request.route)
            bucket.enqueue(request)
            runBucket(bucket)
        })
    }

    fun handleResponse(route: Route, response: Response): Long? {
        bucketLock.lock()
        return try {
            val rateLimit = updateBucket(route, response).rateLimit
            if (response.code == 429) {
                rateLimit
            } else {
                null
            }
        } finally {
            bucketLock.unlock()
        }
    }

    private fun updateBucket(
        route: Route, response: Response
    ): Bucket {
        return locked(bucketLock, Supplier<Bucket> {
            try {
                var bucket = getBucketOrCreate(route)
                val headers = response.headers
                val global = headers[GLOBAL_HEADER] != null
                val hash = headers[HASH_HEADER]
                val now = System.currentTimeMillis()
                if (hash != null) {
                    bucket = getBucketOrCreate(route)
                }
                if (global) {
                    val retryAfterHeader = headers[RETRY_AFTER_HEADER]
                    val retryAfter = parseLong(retryAfterHeader)
                    SessionController.setGlobalRatelimit(now + retryAfter)
                } else if (response.code == 429) {
                    val retryAfterHeader = headers[RETRY_AFTER_HEADER]
                    val retryAfter = parseLong(retryAfterHeader)
                    bucket.remaining = 0
                    bucket.reset = now + retryAfter
                    return@Supplier bucket
                }
                if (hash == null) return@Supplier bucket
                bucket.limit = 1L.coerceAtLeast(parseLong(headers[LIMIT_HEADER])).toInt()
                bucket.remaining = parseLong(headers[REMAINING_HEADER]).toInt()
                bucket.reset = now + parseDouble(headers[RESET_AFTER_HEADER])
                return@Supplier bucket
            } catch (e: Exception) {
                return@Supplier getBucketOrCreate(route)
            }
        })
    }

    private fun getBucket(
        route: Route
    ): Bucket? {
        return locked(bucketLock, Supplier {
            val bucketId = route.method.toString() + "/" + route.baseRoute + ":" + route.majorParameters
            bucket[bucketId]
        })
    }

    private fun getBucketOrCreate(
        route: Route
    ): Bucket {
        return locked(bucketLock, Supplier {
            val bucketId = route.method.toString() + "/" + route.baseRoute + ":" + route.majorParameters
            bucket[bucketId] ?: Bucket(bucketId).also {
                this.bucket[bucketId] = it
            }
        })
    }

    private fun runBucket(bucket: Bucket) {
        locked(bucketLock, Supplier {
            rateLimitQueue.computeIfAbsent(
                bucket
            ) {
                scheduler.schedule(
                    bucket, bucket.rateLimit, TimeUnit.MILLISECONDS
                )
            }
        })
    }

    private fun parseLong(input: String?): Long {
        return input?.toLong() ?: 0L
    }

    private fun parseDouble(input: String?): Long {
        return if (input == null) 0L else (input.toDouble() * 1000).toLong()
    }

    private class Bucket(private val bucketId: String): Runnable {
        val requests: Queue<Request<*>> = ConcurrentLinkedQueue()
        var reset: Long = 0
        var remaining = 1
        var limit = 1
        fun enqueue(request: Request<*>) {
            requests.add(request)
        }

        val rateLimit: Long
            get() {
                val now = System.currentTimeMillis()
                val global = SessionController.getGlobalRatelimit()
                if (now < global) return global - now
                if (reset <= now) {
                    remaining = limit
                    return 0L
                }
                return if (remaining < 1) reset - now else 0L
            }

        val isUnlimited: Boolean
            get() = bucketId.startsWith("unlimited")

        private fun backoff() {
            locked(bucketLock, Runnable {
                rateLimitQueue.remove(this)
                if (!requests.isEmpty()) runBucket(this)
            })
        }

        override fun run() {
            val iterator = requests.iterator()
            while (iterator.hasNext()) {
                if (0L < rateLimit) break
                val request = iterator.next()
                if (isUnlimited) {
                    val shouldSkip = locked(bucketLock, Supplier {
                        val bucket = getBucketOrCreate(request.route)
                        if (bucket !== this) {
                            bucket.enqueue(request)
                            iterator.remove()
                            runBucket(bucket)
                            return@Supplier true
                        }
                        false
                    })
                    if (shouldSkip) continue
                }
                if (isSkipped(iterator, request)) continue
                try {
                    if (Requester.execute(request, false) == null) {
                        iterator.remove()
                    } else {
                        break
                    }
                } catch (ex: Exception) {
                    break
                }
            }
            backoff()
        }

        override fun toString(): String {
            return bucketId
        }

    }
}