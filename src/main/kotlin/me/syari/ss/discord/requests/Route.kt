package me.syari.ss.discord.requests

class Route private constructor(
    val method: Method,
    val baseRoute: String,
    val route: String = baseRoute,
    val majorParameters: String = DEFAULT_MAJOR_PARAMETERS
) {
    enum class Method {
        GET,
        POST
    }

    companion object {
        private const val DEFAULT_MAJOR_PARAMETERS = "guild_id:channel_id:webhook_id"

        val gatewayRoute
            get() = Route(
                Method.GET, "gateway"
            )

        val selfRoute
            get() = Route(
                Method.GET, "users/@me"
            )

        fun sendMessageRoute(channelID: String): Route {
            return Route(
                Method.POST,
                "channels/{channel_id}/messages",
                "channels/$channelID/messages",
                "guild_id:$channelID:webhook_id"
            )
        }
    }
}