package me.syari.ss.discord.internal.requests

import org.jetbrains.annotations.Contract

class Route(val method: Method, val baseRoute: String, val route: String, val majorParameters: String) {
    constructor(method: Method, route: String): this(method, route, route, DEFAULT_MAJOR_PARAMETERS)

    enum class Method {
        GET,
        POST
    }

    companion object {
        private const val DEFAULT_MAJOR_PARAMETERS = "guild_id:channel_id:webhook_id"

        val gatewayRoute: Route
            get() = Route(
                Method.GET, "gateway"
            )

        val selfRoute: Route
            get() = Route(
                Method.GET, "users/@me"
            )

        fun getSendMessageRoute(channelID: String): Route {
            return Route(
                Method.POST,
                "channels/{channel_id}/messages",
                "channels/$channelID/messages",
                "guild_id:$channelID:webhook_id"
            )
        }
    }

}