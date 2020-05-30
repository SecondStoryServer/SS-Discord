package me.syari.ss.discord.exceptions

import me.syari.ss.discord.requests.Route

class RateLimitedException(
    route: String?, retryAfter: Long
): Exception("The request was ratelimited! Retry-After: $retryAfter  Route: $route") {
    internal constructor(route: Route, retryAfter: Long): this("${route.baseRoute}:${route.majorParameters}", retryAfter)
}