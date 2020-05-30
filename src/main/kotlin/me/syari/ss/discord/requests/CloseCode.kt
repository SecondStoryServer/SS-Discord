package me.syari.ss.discord.requests

internal data class CloseCode(private val meaning: String, val isReconnect: Boolean) {
    companion object {
        private val closeCodeMap = mapOf(
            1000 to CloseCode("The connection was closed gracefully or your heartbeats timed out.", true),
            1001 to CloseCode("The connection was closed due to CloudFlare load balancing.", true),
            1006 to CloseCode("Something broke on the remote's end, sorry 'bout that... Try reconnecting!", true),
            4000 to CloseCode("The server is not sure what went wrong. Try reconnecting?", true),
            4001 to CloseCode("You sent an invalid Gateway OP Code. Don't do that!", true),
            4002 to CloseCode("You sent an invalid payload to the server. Don't do that!", true),
            4003 to CloseCode("You sent a payload prior to identifying.", true),
            4004 to CloseCode("The account token sent with your identify payload is incorrect.", false),
            4005 to CloseCode("You sent more than one identify payload. Don't do that!", true),
            4007 to CloseCode("The sent sent when resuming the session was invalid. Reconnect and start a new session.", true),
            4008 to CloseCode("Woah nelly! You're sending payloads to us too quickly. Slow it down!", true),
            4009 to CloseCode("Your session timed out. Reconnect and start a new one.", true),
            4010 to CloseCode("You sent an invalid shard when identifying.", false),
            4011 to CloseCode("The session would have handled too many guilds - you are required to shard your connection in order to connect.", false)
        )

        fun from(code: Int): CloseCode? {
            return closeCodeMap[code]
        }
    }
}