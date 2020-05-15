

package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.internal.requests.Route;

/**
 * Indicates that we received a {@code 429: Too Many Requests} response
 */
public class RateLimitedException extends Exception
{
    private final String rateLimitedRoute;
    private final long retryAfter;

    public RateLimitedException(Route.CompiledRoute route, long retryAfter)
    {
        this(route.getBaseRoute().getRoute() + ":" + route.getMajorParameters(), retryAfter);
    }

    public RateLimitedException(String route, long retryAfter)
    {
        super(String.format("The request was ratelimited! Retry-After: %d  Route: %s", retryAfter, route));
        this.rateLimitedRoute = route;
        this.retryAfter = retryAfter;
    }

    /**
     * The route responsible for the rate limit bucket that is used in
     * the responsible RateLimiter
     *
     * @return The corresponding route
     */
    public String getRateLimitedRoute()
    {
        return rateLimitedRoute;
    }

    /**
     * The back-off delay in milliseconds that should be respected
     * before trying to query the {@link #getRateLimitedRoute() route} again
     *
     * @return The back-off delay in milliseconds
     */
    public long getRetryAfter()
    {
        return retryAfter;
    }
}
