package me.syari.ss.discord.internal.requests;

import org.jetbrains.annotations.NotNull;

public class Route {
    public static final Route GATEWAY = new Route(Method.GET, "gateway", 0);
    public static final Route GET_SELF = new Route(Method.GET, "users/@me", 0);
    public static final Route SEND_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages", 1);

    private static final String majorParameters = "guild_id:channel_id:webhook_id";
    private final String route;
    private final Method method;
    private final int paramCount;

    private Route(Method method, String route, int paramCount) {
        this.method = method;
        this.route = route;
        this.paramCount = paramCount;
    }

    public String getRoute() {
        return route;
    }

    public CompiledRoute compile(String @NotNull ... params) {
        if (params.length != paramCount) {
            throw new IllegalArgumentException("Error Compiling Route: [" + route + "], incorrect amount of parameters provided." +
                    "Expected: " + paramCount + ", Provided: " + params.length);
        }

        StringBuilder majorParameter = new StringBuilder(majorParameters);
        StringBuilder compiledRoute = new StringBuilder(route);
        for (int i = 0; i < paramCount; i++) {
            int paramStart = compiledRoute.indexOf("{");
            int paramEnd = compiledRoute.indexOf("}");
            String paramName = compiledRoute.substring(paramStart + 1, paramEnd);
            int majorParamIndex = majorParameter.indexOf(paramName);
            if (majorParamIndex > -1) majorParameter.replace(majorParamIndex, majorParamIndex + paramName.length(), params[i]);
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

        private CompiledRoute(Route baseRoute, String compiledRoute, String major) {
            this.baseRoute = baseRoute;
            this.compiledRoute = compiledRoute;
            this.major = major;
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
        public boolean equals(Object object) {
            if (!(object instanceof CompiledRoute)) return false;
            CompiledRoute compiledRoute = (CompiledRoute) object;
            return baseRoute.equals(compiledRoute.getBaseRoute()) && this.compiledRoute.equals(compiledRoute.compiledRoute);
        }

        @Override
        public String toString() {
            return "CompiledRoute(" + method + ": " + compiledRoute + ")";
        }
    }

    public enum Method {
        GET,
        POST
    }
}
