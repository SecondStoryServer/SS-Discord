package me.syari.ss.discord.api.exceptions

class ParsingException: IllegalStateException {
    constructor(message: String?): super(message)
    constructor(cause: Exception?): super(cause)
}