package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;


@Deprecated
@ForRemoval
@DeprecatedSince("4.1.0")
public class GuildUnavailableException extends RuntimeException {

    public GuildUnavailableException() {
        this("This operation is not possible due to the Guild being temporarily unavailable");
    }


    public GuildUnavailableException(String reason) {
        super(reason);
    }
}
