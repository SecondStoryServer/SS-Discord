package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.entities.Guild;

public class VerificationLevelException extends IllegalStateException {
    public VerificationLevelException(Guild.VerificationLevel level) {
        super("Messages to this Guild can not be sent due to the Guilds verification level. (" + level.toString() + ')');
    }
}
