package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.internal.utils.Helpers;

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

    public String getRoute() {
        return route;
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
