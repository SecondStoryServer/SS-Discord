

package me.syari.ss.discord.internal.requests;

import me.syari.ss.discord.internal.utils.Checks;
import me.syari.ss.discord.internal.utils.Helpers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Route
{
    public static class Misc
    {
        public static final Route TRACK =             new Route(Method.POST, "track");
        public static final Route GET_VOICE_REGIONS = new Route(Method.GET,  "voice/regions");
        public static final Route GATEWAY =           new Route(Method.GET,  "gateway");
        public static final Route GATEWAY_BOT =       new Route(Method.GET,  "gateway/bot");
    }

    public static class Applications
    {
        // Bot only
        public static final Route GET_BOT_APPLICATION =           new Route(Method.GET,    "oauth2/applications/@me");

        // Client only
        public static final Route GET_APPLICATIONS =              new Route(Method.GET,    "oauth2/applications");
        public static final Route CREATE_APPLICATION =            new Route(Method.POST,   "oauth2/applications");
        public static final Route GET_APPLICATION =               new Route(Method.GET,    "oauth2/applications/{application_id}");
        public static final Route MODIFY_APPLICATION =            new Route(Method.PUT,    "oauth2/applications/{application_id}");
        public static final Route DELETE_APPLICATION =            new Route(Method.DELETE, "oauth2/applications/{application_id}");

        public static final Route CREATE_BOT =                    new Route(Method.POST,   "oauth2/applications/{application_id}/bot");

        public static final Route RESET_APPLICATION_SECRET =      new Route(Method.POST,   "oauth2/applications/{application_id}/reset");
        public static final Route RESET_BOT_TOKEN =               new Route(Method.POST,   "oauth2/applications/{application_id}/bot/reset");

        public static final Route GET_AUTHORIZED_APPLICATIONS =   new Route(Method.GET,    "oauth2/tokens");
        public static final Route GET_AUTHORIZED_APPLICATION =    new Route(Method.GET,    "oauth2/tokens/{auth_id}");
        public static final Route DELETE_AUTHORIZED_APPLICATION = new Route(Method.DELETE, "oauth2/tokens/{auth_id}");
    }

    public static class Self
    {
        public static final Route GET_SELF =               new Route(Method.GET,    "users/@me");
        public static final Route MODIFY_SELF =            new Route(Method.PATCH,  "users/@me");
        public static final Route GET_GUILDS  =            new Route(Method.GET,    "users/@me/guilds");
        public static final Route LEAVE_GUILD =            new Route(Method.DELETE, "users/@me/guilds/{guild_id}");
        public static final Route GET_PRIVATE_CHANNELS =   new Route(Method.GET,    "users/@me/channels");
        public static final Route CREATE_PRIVATE_CHANNEL = new Route(Method.POST,   "users/@me/channels");

        // Client only
        public static final Route USER_SETTINGS =       new Route(Method.GET, "users/@me/settings");
        public static final Route GET_CONNECTIONS =     new Route(Method.GET, "users/@me/connections");
        public static final Route FRIEND_SUGGESTIONS =  new Route(Method.GET, "friend-suggestions");
        public static final Route GET_RECENT_MENTIONS = new Route(Method.GET, "users/@me/mentions");
    }

    public static class Users
    {
        public static final Route GET_USER    = new Route(Method.GET, "users/{user_id}");
        public static final Route GET_PROFILE = new Route(Method.GET, "users/{user_id}/profile");
        public static final Route GET_NOTE    = new Route(Method.GET, "users/@me/notes/{user_id}");
        public static final Route SET_NOTE    = new Route(Method.PUT, "users/@me/notes/{user_id}");
    }

    public static class Relationships
    {
        public static final Route GET_RELATIONSHIPS =   new Route(Method.GET,    "users/@me/relationships"); // Get Friends/Blocks/Incoming/Outgoing
        public static final Route GET_RELATIONSHIP =    new Route(Method.GET,    "users/@me/relationships/{user_id}");
        public static final Route ADD_RELATIONSHIP =    new Route(Method.PUT,    "users/@me/relationships/{user_id}"); // Add Friend/ Block
        public static final Route DELETE_RELATIONSHIP = new Route(Method.DELETE, "users/@me/relationships/{user_id}"); // Delete Block/Unfriend/Ignore Request/Cancel Outgoing
    }

    public static class Guilds
    {
        public static final Route GET_GUILD =          new Route(Method.GET,    "guilds/{guild_id}");
        public static final Route MODIFY_GUILD =       new Route(Method.PATCH,  "guilds/{guild_id}");
        public static final Route GET_VANITY_URL =     new Route(Method.GET,    "guilds/{guild_id}/vanity-url");
        public static final Route CREATE_CHANNEL =     new Route(Method.POST,   "guilds/{guild_id}/channels");
        public static final Route GET_CHANNELS =       new Route(Method.GET,    "guilds/{guild_id}/channels");
        public static final Route MODIFY_CHANNELS =    new Route(Method.PATCH,  "guilds/{guild_id}/channels");
        public static final Route MODIFY_ROLES =       new Route(Method.PATCH,  "guilds/{guild_id}/roles");
        public static final Route GET_BANS =           new Route(Method.GET,    "guilds/{guild_id}/bans");
        public static final Route GET_BAN =            new Route(Method.GET,    "guilds/{guild_id}/bans/{user_id}");
        public static final Route UNBAN =              new Route(Method.DELETE, "guilds/{guild_id}/bans/{user_id}");
        public static final Route BAN =                new Route(Method.PUT,    "guilds/{guild_id}/bans/{user_id}");
        public static final Route KICK_MEMBER =        new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}");
        public static final Route MODIFY_MEMBER =      new Route(Method.PATCH,  "guilds/{guild_id}/members/{user_id}");
        public static final Route ADD_MEMBER =         new Route(Method.PUT,    "guilds/{guild_id}/members/{user_id}");
        public static final Route GET_MEMBER =         new Route(Method.GET,    "guilds/{guild_id}/members/{user_id}");
        public static final Route MODIFY_SELF_NICK =   new Route(Method.PATCH,  "guilds/{guild_id}/members/@me/nick");
        public static final Route PRUNABLE_COUNT =     new Route(Method.GET,    "guilds/{guild_id}/prune");
        public static final Route PRUNE_MEMBERS =      new Route(Method.POST,   "guilds/{guild_id}/prune");
        public static final Route GET_WEBHOOKS =       new Route(Method.GET,    "guilds/{guild_id}/webhooks");
        public static final Route GET_GUILD_EMBED =    new Route(Method.GET,    "guilds/{guild_id}/embed");
        public static final Route MODIFY_GUILD_EMBED = new Route(Method.PATCH,  "guilds/{guild_id}/embed");
        public static final Route GET_GUILD_EMOTES =   new Route(Method.GET,    "guilds/{guild_id}/emojis");
        public static final Route GET_AUDIT_LOGS =     new Route(Method.GET,    "guilds/{guild_id}/audit-logs");
        public static final Route GET_VOICE_REGIONS =  new Route(Method.GET,    "guilds/{guild_id}/regions");

        public static final Route GET_INTEGRATIONS =   new Route(Method.GET,    "guilds/{guild_id}/integrations");
        public static final Route CREATE_INTEGRATION = new Route(Method.POST,   "guilds/{guild_id}/integrations");
        public static final Route DELETE_INTEGRATION = new Route(Method.DELETE, "guilds/{guild_id}/integrations/{integration_id}");
        public static final Route MODIFY_INTEGRATION = new Route(Method.PATCH,  "guilds/{guild_id}/integrations/{integration_id}");
        public static final Route SYNC_INTEGRATION =   new Route(Method.POST,   "guilds/{guild_id}/integrations/{integration_id}/sync");

        public static final Route ADD_MEMBER_ROLE =    new Route(Method.PUT,    "guilds/{guild_id}/members/{user_id}/roles/{role_id}");
        public static final Route REMOVE_MEMBER_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}/roles/{role_id}");


        //Client Only
        public static final Route CREATE_GUILD = new Route(Method.POST, "guilds");
        public static final Route DELETE_GUILD = new Route(Method.POST, "guilds/{guild_id}/delete");
        public static final Route ACK_GUILD =    new Route(Method.POST, "guilds/{guild_id}/ack");

        public static final Route MODIFY_NOTIFICATION_SETTINGS = new Route(Method.PATCH, "users/@me/guilds/{guild_id}/settings");
    }

    public static class Emotes
    {
        // These are all client endpoints and thus don't need defined major parameters
        public static final Route MODIFY_EMOTE = new Route(Method.PATCH,  "guilds/{guild_id}/emojis/{emote_id}");
        public static final Route DELETE_EMOTE = new Route(Method.DELETE, "guilds/{guild_id}/emojis/{emote_id}");
        public static final Route CREATE_EMOTE = new Route(Method.POST,   "guilds/{guild_id}/emojis");

        public static final Route GET_EMOTES   = new Route(Method.GET,    "guilds/{guild_id}/emojis");
        public static final Route GET_EMOTE    = new Route(Method.GET,    "guilds/{guild_id}/emojis/{emoji_id}");
    }

    public static class Webhooks
    {
        public static final Route GET_WEBHOOK          = new Route(Method.GET,    "webhooks/{webhook_id}");
        public static final Route GET_TOKEN_WEBHOOK    = new Route(Method.GET,    "webhooks/{webhook_id}/{token}");
        public static final Route DELETE_WEBHOOK       = new Route(Method.DELETE, "webhooks/{webhook_id}");
        public static final Route DELETE_TOKEN_WEBHOOK = new Route(Method.DELETE, "webhooks/{webhook_id}/{token}");
        public static final Route MODIFY_WEBHOOK       = new Route(Method.PATCH,  "webhooks/{webhook_id}");
        public static final Route MODIFY_TOKEN_WEBHOOK = new Route(Method.PATCH,  "webhooks/{webhook_id}/{token}");

        // Separate
        public static final Route EXECUTE_WEBHOOK        = new Route(Method.POST, "webhooks/{webhook_id}/{token}");
        public static final Route EXECUTE_WEBHOOK_SLACK  = new Route(Method.POST, "webhooks/{webhook_id}/{token}/slack");
        public static final Route EXECUTE_WEBHOOK_GITHUB = new Route(Method.POST, "webhooks/{webhook_id}/{token}/github");
    }

    public static class Roles
    {
        public static final Route GET_ROLES =   new Route(Method.GET,    "guilds/{guild_id}/roles");
        public static final Route CREATE_ROLE = new Route(Method.POST,   "guilds/{guild_id}/roles");
        public static final Route GET_ROLE =    new Route(Method.GET,    "guilds/{guild_id}/roles/{role_id}");
        public static final Route MODIFY_ROLE = new Route(Method.PATCH,  "guilds/{guild_id}/roles/{role_id}");
        public static final Route DELETE_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/roles/{role_id}");
    }

    public static class Channels
    {
        public static final Route DELETE_CHANNEL =       new Route(Method.DELETE, "channels/{channel_id}");
        public static final Route MODIFY_CHANNEL =       new Route(Method.PATCH,  "channels/{channel_id}");
        public static final Route GET_WEBHOOKS =         new Route(Method.GET,    "channels/{channel_id}/webhooks");
        public static final Route CREATE_WEBHOOK =       new Route(Method.POST,   "channels/{channel_id}/webhooks");
        public static final Route CREATE_PERM_OVERRIDE = new Route(Method.PUT,    "channels/{channel_id}/permissions/{permoverride_id}");
        public static final Route MODIFY_PERM_OVERRIDE = new Route(Method.PUT,    "channels/{channel_id}/permissions/{permoverride_id}");
        public static final Route DELETE_PERM_OVERRIDE = new Route(Method.DELETE, "channels/{channel_id}/permissions/{permoverride_id}");

        public static final Route SEND_TYPING =          new Route(Method.POST,   "channels/{channel_id}/typing");
        public static final Route GET_PERMISSIONS =      new Route(Method.GET,    "channels/{channel_id}/permissions");
        public static final Route GET_PERM_OVERRIDE =    new Route(Method.GET,    "channels/{channel_id}/permissions/{permoverride_id}");

        // Client Only
        public static final Route GET_RECIPIENTS =   new Route(Method.GET,    "channels/{channel_id}/recipients");
        public static final Route GET_RECIPIENT =    new Route(Method.GET,    "channels/{channel_id}/recipients/{user_id}");
        public static final Route ADD_RECIPIENT =    new Route(Method.PUT,    "channels/{channel_id}/recipients/{user_id}");
        public static final Route REMOVE_RECIPIENT = new Route(Method.DELETE, "channels/{channel_id}/recipients/{user_id}");
        public static final Route START_CALL =       new Route(Method.POST,   "channels/{channel_id}/call/ring");
        public static final Route STOP_CALL =        new Route(Method.POST,   "channels/{channel_id}/call/stop_ringing"); // aka deny or end call
    }

    public static class Messages
    {
        public static final Route EDIT_MESSAGE =          new Route(Method.PATCH,  "channels/{channel_id}/messages/{message_id}"); // requires special handling, same bucket but different endpoints
        public static final Route SEND_MESSAGE =          new Route(Method.POST,   "channels/{channel_id}/messages");
        public static final Route GET_PINNED_MESSAGES =   new Route(Method.GET,    "channels/{channel_id}/pins");
        public static final Route ADD_PINNED_MESSAGE =    new Route(Method.PUT,    "channels/{channel_id}/pins/{message_id}");
        public static final Route REMOVE_PINNED_MESSAGE = new Route(Method.DELETE, "channels/{channel_id}/pins/{message_id}");

        public static final Route ADD_REACTION =             new Route(Method.PUT,    "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}");
        public static final Route REMOVE_REACTION =          new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}");
        public static final Route REMOVE_ALL_REACTIONS =     new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions");
        public static final Route GET_REACTION_USERS =       new Route(Method.GET,    "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}");

        public static final Route DELETE_MESSAGE =      new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}");
        public static final Route GET_MESSAGE_HISTORY = new Route(Method.GET,    "channels/{channel_id}/messages");

        //Bot only
        public static final Route GET_MESSAGE =     new Route(Method.GET,  "channels/{channel_id}/messages/{message_id}");
        public static final Route DELETE_MESSAGES = new Route(Method.POST, "channels/{channel_id}/messages/bulk-delete");

        //Client only
        public static final Route ACK_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages/{message_id}/ack");
    }

    public static class Invites
    {
        public static final Route GET_INVITE =          new Route(Method.GET,    "invites/{code}");
        public static final Route GET_GUILD_INVITES =   new Route(Method.GET,    "guilds/{guild_id}/invites");
        public static final Route GET_CHANNEL_INVITES = new Route(Method.GET,    "channels/{channel_id}/invites");
        public static final Route CREATE_INVITE =       new Route(Method.POST,   "channels/{channel_id}/invites");
        public static final Route DELETE_INVITE =       new Route(Method.DELETE, "invites/{code}");
    }

    @Nonnull
    public static Route custom(@Nonnull Method method, @Nonnull String route)
    {
        Checks.notNull(method, "Method");
        Checks.notEmpty(route, "Route");
        Checks.noWhitespace(route, "Route");
        return new Route(method, route);
    }

    @Nonnull
    public static Route delete(@Nonnull String route)
    {
        return custom(Method.DELETE, route);
    }

    @Nonnull
    public static Route post(@Nonnull String route)
    {
        return custom(Method.POST, route);
    }

    @Nonnull
    public static Route put(@Nonnull String route)
    {
        return custom(Method.PUT, route);
    }

    @Nonnull
    public static Route patch(@Nonnull String route)
    {
        return custom(Method.PATCH, route);
    }

    @Nonnull
    public static Route get(@Nonnull String route)
    {
        return custom(Method.GET, route);
    }

    private static final String majorParameters = "guild_id:channel_id:webhook_id";
    private final String route;
    private final Method method;
    private final int paramCount;

    private Route(Method method, String route)
    {
        this.method = method;
        this.route = route;
        this.paramCount = Helpers.countMatches(route, '{'); //All parameters start with {

        if (paramCount != Helpers.countMatches(route, '}'))
            throw new IllegalArgumentException("An argument does not have both {}'s for route: " + method + "  " + route);
    }

    public Method getMethod()
    {
        return method;
    }

    public String getRoute()
    {
        return route;
    }

    public int getParamCount()
    {
        return paramCount;
    }

    public CompiledRoute compile(String... params)
    {
        if (params.length != paramCount)
        {
            throw new IllegalArgumentException("Error Compiling Route: [" + route + "], incorrect amount of parameters provided." +
                    "Expected: " + paramCount + ", Provided: " + params.length);
        }

        //Compile the route for interfacing with discord.

        StringBuilder majorParameter = new StringBuilder(majorParameters);
        StringBuilder compiledRoute = new StringBuilder(route);
        for (int i = 0; i < paramCount; i++)
        {
            int paramStart = compiledRoute.indexOf("{");
            int paramEnd = compiledRoute.indexOf("}");
            String paramName = compiledRoute.substring(paramStart+1, paramEnd);
            int majorParamIndex = majorParameter.indexOf(paramName);
            if (majorParamIndex > -1)
                majorParameter.replace(majorParamIndex, majorParamIndex + paramName.length(), params[i]);

            compiledRoute.replace(paramStart, paramEnd + 1, params[i]);
        }

        return new CompiledRoute(this, compiledRoute.toString(), majorParameter.toString());
    }

    @Override
    public int hashCode()
    {
        return (route + method.toString()).hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Route))
            return false;

        Route oRoute = (Route) o;
        return method.equals(oRoute.method) && route.equals(oRoute.route);
    }

    @Override
    public String toString()
    {
        return method + "/" + route;
    }

    public class CompiledRoute
    {
        private final Route baseRoute;
        private final String major;
        private final String compiledRoute;
        private final boolean hasQueryParams; 

        private CompiledRoute(Route baseRoute, String compiledRoute, String major, boolean hasQueryParams)
        {
            this.baseRoute = baseRoute;
            this.compiledRoute = compiledRoute;
            this.major = major;
            this.hasQueryParams = hasQueryParams;
        }

        private CompiledRoute(Route baseRoute, String compiledRoute, String major)
        {
            this(baseRoute, compiledRoute, major, false);
        }

        @Nonnull
        @CheckReturnValue
        public CompiledRoute withQueryParams(String... params)
        {
            Checks.check(params.length >= 2, "params length must be at least 2");
            Checks.check(params.length % 2 == 0, "params length must be a multiple of 2");

            StringBuilder newRoute = new StringBuilder(compiledRoute);

            for (int i = 0; i < params.length; i++)
                newRoute.append(!hasQueryParams && i == 0 ? '?' : '&').append(params[i]).append('=').append(params[++i]);

            return new CompiledRoute(baseRoute, newRoute.toString(), major, true);
        }

        public String getMajorParameters()
        {
            return major;
        }

        public String getCompiledRoute()
        {
            return compiledRoute;
        }

        public Route getBaseRoute()
        {
            return baseRoute;
        }

        public Method getMethod()
        {
            return baseRoute.method;
        }

        @Override
        public int hashCode()
        {
            return (compiledRoute + method.toString()).hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof CompiledRoute))
                return false;

            CompiledRoute oCompiled = (CompiledRoute) o;

            return baseRoute.equals(oCompiled.getBaseRoute()) && compiledRoute.equals(oCompiled.compiledRoute);
        }

        @Override
        public String toString()
        {
            return "CompiledRoute(" + method + ": " + compiledRoute + ")";
        }
    }
}
