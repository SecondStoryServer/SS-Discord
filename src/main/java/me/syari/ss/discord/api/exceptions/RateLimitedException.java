package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.internal.requests.Route;


public class RateLimitedException extends Exception {
    private final String rateLimitedRoute;
    private final long retryAfter;

    public RateLimitedException(Route.CompiledRoute route, long retryAfter) {
        this(route.getBaseRoute().getRoute() + ":" + route.getMajorParameters(), retryAfter);
    }

    public RateLimitedException(String route, long retryAfter) {
        super(String.format("The request was ratelimited! Retry-After: %d  Route: %s", retryAfter, route));
        this.rateLimitedRoute = route;
        this.retryAfter = retryAfter;
    }


    public String getRateLimitedRoute() {
        return rateLimitedRoute;
    }


    public long getRetryAfter() {
        return retryAfter;
    }
}
