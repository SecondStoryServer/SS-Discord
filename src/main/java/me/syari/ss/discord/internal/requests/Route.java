package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Route {
    public static class Misc {
        public static final Route GATEWAY = new Route(Method.GET, "gateway");
    }

    public static class Self {
        public static final Route GET_SELF = new Route(Method.GET, "users/@me");
    }

    public static class Messages {
        public static final Route SEND_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages");
    }

    @Nonnull
    public static Route custom(@Nonnull Method method, @Nonnull String route) {
        Checks.notNull(method, "Method");
        Checks.notEmpty(route, "Route");
        Checks.noWhitespace(route, "Route");
        return new Route(method, route);
    }

    @Nonnull
    public static Route delete(@Nonnull String route) {
        return custom(Method.DELETE, route);
    }

    @Nonnull
    public static Route post(@Nonnull String route) {
        return custom(Method.POST, route);
    }

    @Nonnull
    public static Route put(@Nonnull String route) {
        return custom(Method.PUT, route);
    }

    @Nonnull
    public static Route patch(@Nonnull String route) {
        return custom(Method.PATCH, route);
    }

    @Nonnull
    public static Route get(@Nonnull String route) {
        return custom(Method.GET, route);
    }

    private static final String majorParameters = "guild_id:channel_id:webhook_id";
    private final String route;
    private final Method method;
    private final int paramCount;

    private Route(Method method, String route) {
        this.method = method;
        this.route = route;
        this.paramCount = Helpers.countMatches(route, '{'); //All parameters start with {

        if (paramCount != Helpers.countMatches(route, '}'))
            throw new IllegalArgumentException("An argument does not have both {}'s for route: " + method + "  " + route);
    }

    public Method getMethod() {
        return method;
    }

    public String getRoute() {
        return route;
    }

    public int getParamCount() {
        return paramCount;
    }

    public CompiledRoute compile(String... params) {
        if (params.length != paramCount) {
            throw new IllegalArgumentException("Error Compiling Route: [" + route + "], incorrect amount of parameters provided." +
                    "Expected: " + paramCount + ", Provided: " + params.length);
        }

        //Compile the route for interfacing with discord.

        StringBuilder majorParameter = new StringBuilder(majorParameters);
        StringBuilder compiledRoute = new StringBuilder(route);
        for (int i = 0; i < paramCount; i++) {
            int paramStart = compiledRoute.indexOf("{");
            int paramEnd = compiledRoute.indexOf("}");
            String paramName = compiledRoute.substring(paramStart + 1, paramEnd);
            int majorParamIndex = majorParameter.indexOf(paramName);
            if (majorParamIndex > -1)
                majorParameter.replace(majorParamIndex, majorParamIndex + paramName.length(), params[i]);

            compiledRoute.replace(paramStart, paramEnd + 1, params[i]);
        }

        return new CompiledRoute(this, compiledRoute.toString(), majorParameter.toString());
    }

    @Override
    public int hashCode() {
        return (route + method.toString()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Route))
            return false;

        Route oRoute = (Route) o;
        return method.equals(oRoute.method) && route.equals(oRoute.route);
    }

    @Override
    public String toString() {
        return method + "/" + route;
    }

    public class CompiledRoute {
        private final Route baseRoute;
        private final String major;
        private final String compiledRoute;
        private final boolean hasQueryParams;

        private CompiledRoute(Route baseRoute, String compiledRoute, String major, boolean hasQueryParams) {
            this.baseRoute = baseRoute;
            this.compiledRoute = compiledRoute;
            this.major = major;
            this.hasQueryParams = hasQueryParams;
        }

        private CompiledRoute(Route baseRoute, String compiledRoute, String major) {
            this(baseRoute, compiledRoute, major, false);
        }

        @Nonnull
        @CheckReturnValue
        public CompiledRoute withQueryParams(String... params) {
            Checks.check(params.length >= 2, "params length must be at least 2");
            Checks.check(params.length % 2 == 0, "params length must be a multiple of 2");

            StringBuilder newRoute = new StringBuilder(compiledRoute);

            for (int i = 0; i < params.length; i++)
                newRoute.append(!hasQueryParams && i == 0 ? '?' : '&').append(params[i]).append('=').append(params[++i]);

            return new CompiledRoute(baseRoute, newRoute.toString(), major, true);
        }

        public String getMajorParameters() {
            return major;
        }

        public String getCompiledRoute() {
            return compiledRoute;
        }

        public Route getBaseRoute() {
            return baseRoute;
        }

        public Method getMethod() {
            return baseRoute.method;
        }

        @Override
        public int hashCode() {
            return (compiledRoute + method.toString()).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompiledRoute))
                return false;

            CompiledRoute oCompiled = (CompiledRoute) o;

            return baseRoute.equals(oCompiled.getBaseRoute()) && compiledRoute.equals(oCompiled.compiledRoute);
        }

        @Override
        public String toString() {
            return "CompiledRoute(" + method + ": " + compiledRoute + ")";
        }
    }
}
