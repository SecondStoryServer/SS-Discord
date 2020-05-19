package me.syari.ss.discord.api.requests

import org.jetbrains.annotations.Contract

enum class CloseCode(private val code: Int, private val meaning: String, val isReconnect: Boolean) {
    GRACEFUL_CLOSE(1000, "The connection was closed gracefully or your heartbeats timed out.", true),
    CLOUD_FLARE_LOAD(1001, "The connection was closed due to CloudFlare load balancing.", true),
    INTERNAL_SERVER_ERROR(1006, "Something broke on the remote's end, sorry 'bout that... Try reconnecting!", true),
    UNKNOWN_ERROR(4000, "The server is not sure what went wrong. Try reconnecting?", true),
    UNKNOWN_OPCODE(4001, "You sent an invalid Gateway OP Code. Don't do that!", true),
    DECODE_ERROR(4002, "You sent an invalid payload to the server. Don't do that!", true),
    NOT_AUTHENTICATED(4003, "You sent a payload prior to identifying.", true),
    AUTHENTICATION_FAILED(4004, "The account token sent with your identify payload is incorrect.", false),
    ALREADY_AUTHENTICATED(4005, "You sent more than one identify payload. Don't do that!", true),
    INVALID_SEQ(4007, "The sent sent when resuming the session was invalid. Reconnect and start a new session.", true),
    RATE_LIMITED(4008, "Woah nelly! You're sending payloads to us too quickly. Slow it down!", true),
    SESSION_TIMEOUT(4009, "Your session timed out. Reconnect and start a new one.", true),
    INVALID_SHARD(4010, "You sent an invalid shard when identifying.", false),
    SHARDING_REQUIRED(
        4011,
        "The session would have handled too many guilds - you are required to shard your connection in order to connect.",
        false
    );

    @Contract(pure = true)
    override fun toString(): String {
        return "CloseCode($code / $meaning)"
    }

    companion object {
        @JvmStatic
        fun from(code: Int): CloseCode? {
            for (c in values()) if (c.code == code) return c
            return null
        }
    }
}