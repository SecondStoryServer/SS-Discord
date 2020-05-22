package me.syari.ss.discord.api.requests

import me.syari.ss.discord.api.data.DataContainer

data class ErrorResponse(val code: Int, val meaning: String) {
    companion object {
        private val errorCodeMap = mapOf(
            10001 to "Unknown Account",
            10002 to "Unknown Application",
            10003 to "Unknown Channel",
            10004 to "Unknown Guild",
            10005 to "Unknown Integration",
            10006 to "Unknown Invite",
            10007 to "Unknown Member",
            10008 to "Unknown Message",
            10009 to "Unknown Override",
            10010 to "Unknown Provider",
            10011 to "Unknown Role",
            10012 to "Unknown Token",
            10013 to "Unknown User",
            10014 to "Unknown Emoji",
            10015 to "Unknown Webhook",
            10026 to "Unknown Ban",
            20001 to "Bots cannot use this endpoint",
            20002 to "Only bots can use this endpoint",
            30001 to "Maximum number of Guilds reached (100)",
            30002 to "Maximum number of Friends reached (1000)",
            30003 to "Maximum number of pinned messages reached (50)",
            30004 to "Maximum number of recipients reached. (10)",
            30005 to "Maximum number of guild roles reached (250)",
            30010 to "Too many reactions",
            30013 to "Maximum number of guild channels reached (500)",
            40001 to "Unauthorized",
            40032 to "Target user is not connected to voice.",
            50001 to "Missing Access",
            50002 to "Invalid Account Type",
            50003 to "Cannot execute action on a DM channel",
            50004 to "Widget Disabled",
            50005 to "Cannot edit a message authored by another user",
            50006 to "Cannot send an empty message",
            50007 to "Cannot send messages to this user",
            50008 to "Cannot send messages in a voice channel",
            50009 to "Channel verification level is too high",
            50010 to "OAuth2 application does not have a bot",
            50011 to "OAuth2 application limit reached",
            50012 to "Invalid OAuth state",
            50013 to "Missing Permissions",
            50014 to "Invalid Authentication Token",
            50015 to "Note is too long",
            50016 to "Provided too few or too many messages to delete. Must provided at least 2 and fewer than 100 messages to delete",
            50017 to "Provided MFA level was invalid.",
            50018 to "Provided password was invalid",
            50019 to "A message can only be pinned to the channel it was sent in",
            50020 to "Invite code is either invalid or taken",
            50021 to "Cannot execute action on a system message",
            50025 to "Invalid OAuth2 access token",
            50027 to "Invalid Webhook Token",
            50034 to "A Message provided to bulk_delete was older than 2 weeks",
            50035 to "Invalid Form Body",
            50036 to "An invite was accepted to a guild the application's bot is not in",
            50041 to "Invalid API version",
            60003 to "MFA auth required but not enabled",
            90001 to "Reaction Blocked",
            130000 to "Resource overloaded"
        )

        private val SERVER_ERROR = ErrorResponse(0, "Discord encountered an internal server error! Not good!")

        private fun fromCode(code: Int?): ErrorResponse {
            return code?.let {
                errorCodeMap[code]?.let { meaning ->
                    ErrorResponse(code, meaning)
                }
            } ?: SERVER_ERROR
        }

        fun fromJSON(dataObject: DataContainer?): ErrorResponse {
            return fromCode(dataObject?.getInt("code"))
        }
    }
}