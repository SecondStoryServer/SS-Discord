package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

public class RateLimitedException extends Exception {
    public RateLimitedException(Route.@NotNull CompiledRoute route, long retryAfter) {
        this(route.getBaseRoute().getRoute() + ":" + route.getMajorParameters(), retryAfter);
    }

    public RateLimitedException(String route, long retryAfter) {
        super(String.format("The request was ratelimited! Retry-After: %d  Route: %s", retryAfter, route));
    }
}
