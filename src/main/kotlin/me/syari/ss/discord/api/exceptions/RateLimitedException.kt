package me.syari.ss.discord.api.exceptions

import me.syari.ss.discord.internal.requests.Route

class RateLimitedException(
    route: String?, retryAfter: Long
): Exception(String.format("The request was ratelimited! Retry-After: %d  Route: %s", retryAfter, route)) {
    constructor(route: Route, retryAfter: Long): this(route.baseRoute + ":" + route.majorParameters, retryAfter)
}