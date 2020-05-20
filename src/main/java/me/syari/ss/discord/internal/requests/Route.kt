package me.syari.ss.discord.internal.requests;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Route {
    private static final String DEFAULT_MAJOR_PARAMETERS = "guild_id:channel_id:webhook_id";

    @Contract(value = " -> new", pure = true)
    public static @NotNull
    Route getGatewayRoute() {
        return new Route(Method.GET, "gateway");
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull
    Route getSelfRoute() {
        return new Route(Method.GET, "users/@me");
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull
    Route getSendMessageRoute(String channelID) {
        return new Route(Method.POST, "channels/{channel_id}/messages", "channels/" + channelID + "/messages", "guild_id:" + channelID + ":webhook_id");
    }

    private final Method method;
    private final String baseRoute;
    private final String route;
    private final String majorParameters;


    public Route(@NotNull Method method, @NotNull String route) {
        this(method, route, route, DEFAULT_MAJOR_PARAMETERS);
    }

    public Route(@NotNull Method method, @NotNull String baseRoute, @NotNull String route, @NotNull String majorParameters) {
        this.method = method;
        this.baseRoute = baseRoute;
        this.route = route;
        this.majorParameters = majorParameters;
    }

    @NotNull
    public Method getMethod() {
        return method;
    }

    @NotNull
    public String getBaseRoute() {
        return baseRoute;
    }

    @NotNull
    public String getRoute() {
        return route;
    }

    @NotNull
    public String getMajorParameters() {
        return majorParameters;
    }

    public enum Method {
        GET,
        POST
    }
}
