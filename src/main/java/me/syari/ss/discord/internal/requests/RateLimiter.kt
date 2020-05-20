package me.syari.ss.discord.internal.requests

import me.syari.ss.discord.api.requests.Request
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

class RateLimiter(private val requester: Requester) {
    private val bucketLock = ReentrantLock()
    private val bucket: MutableMap<String, Bucket> = ConcurrentHashMap()
    private val rateLimitQueue: MutableMap<Bucket?, Future<*>> = ConcurrentHashMap()
    private var cleanupWorker: Future<*>? = null
    fun init() {
        cleanupWorker = scheduler.scheduleAtFixedRate({ cleanup() }, 30, 30, TimeUnit.SECONDS)
    }

    private val scheduler: ScheduledExecutorService
        get() = requester.jda.rateLimitPool

    private fun cleanup() {
        locked(bucketLock, Runnable {
            val entries: MutableIterator<Map.Entry<String, Bucket>> = bucket.entries.iterator()
            while (entries.hasNext()) {
                val entry = entries.next()
                val bucket = entry.value
                if (bucket.isUnlimited && bucket.requests.isEmpty() || bucket.requests.isEmpty() && bucket.reset <= now) {
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
        cleanupWorker?.cancel(false)
    }

    fun getRateLimit(route: Route): Long {
        val bucket = getBucket(route, false)
        return bucket?.rateLimit ?: 0L
    }

    fun queueRequest(request: Request<*>) {
        locked(bucketLock, Runnable {
            val bucket = getBucket(request.route, true)
            bucket!!.enqueue(request)
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
                var bucket = getBucket(route, true)
                val headers = response.headers
                val global = headers[GLOBAL_HEADER] != null
                val hash = headers[HASH_HEADER]
                val now = now
                if (hash != null) {
                    bucket = getBucket(route, true)
                }
                if (global) {
                    val retryAfterHeader = headers[RETRY_AFTER_HEADER]
                    val retryAfter = parseLong(retryAfterHeader)
                    requester.jda.sessionController.setGlobalRatelimit(now + retryAfter)
                } else if (response.code == 429) {
                    val retryAfterHeader = headers[RETRY_AFTER_HEADER]
                    val retryAfter = parseLong(retryAfterHeader)
                    bucket!!.remaining = 0
                    bucket.reset = now + retryAfter
                    return@Supplier bucket
                }
                if (hash == null) return@Supplier bucket!!
                bucket!!.limit = Math.max(1L, parseLong(headers[LIMIT_HEADER])).toInt()
                bucket.remaining = parseLong(headers[REMAINING_HEADER]).toInt()
                bucket.reset = now + parseDouble(headers[RESET_AFTER_HEADER])
                return@Supplier bucket
            } catch (e: Exception) {
                return@Supplier getBucket(route, true)!!
            }
        })
    }

    private fun getBucket(
        route: Route, create: Boolean
    ): Bucket? {
        return locked(bucketLock, Supplier {
            val bucketId = route.method.toString() + "/" + route.baseRoute + ":" + route.majorParameters
            var bucket = bucket[bucketId]
            if (bucket == null && create) {
                this.bucket[bucketId] = Bucket(bucketId).also { bucket = it }
            }
            bucket
        })
    }

    private fun runBucket(bucket: Bucket?) {
        locked(bucketLock, Supplier {
            rateLimitQueue.computeIfAbsent(
                bucket
            ) {
                scheduler.schedule(
                    bucket, bucket!!.rateLimit, TimeUnit.MILLISECONDS
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

    val now: Long
        get() = System.currentTimeMillis()

    private inner class Bucket(private val bucketId: String): Runnable {
        val requests: Queue<Request<*>> = ConcurrentLinkedQueue()
        var reset: Long = 0
        var remaining = 1
        var limit = 1
        fun enqueue(request: Request<*>) {
            requests.add(request)
        }

        val rateLimit: Long
            get() {
                val now = now
                val global = requester.jda.sessionController.getGlobalRatelimit()
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
                var rateLimit: Long? = rateLimit
                if (0L < rateLimit!!) break
                val request = iterator.next()
                if (isUnlimited) {
                    val shouldSkip = locked(bucketLock, Supplier {
                        val bucket = getBucket(request.route, true)
                        if (bucket !== this) {
                            bucket!!.enqueue(request)
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
                    rateLimit = requester.execute(request, false)
                    if (rateLimit == null) {
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

    companion object {
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
    }

}