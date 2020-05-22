package me.syari.ss.discord.requests

object WebSocketCode {
    const val DISPATCH = 0
    const val HEARTBEAT = 1
    const val IDENTIFY = 2
    const val RESUME = 6
    const val RECONNECT = 7
    const val MEMBER_CHUNK_REQUEST = 8
    const val INVALIDATE_SESSION = 9
    const val HELLO = 10
}