
package me.syari.ss.discord.api.exceptions;

import me.syari.ss.discord.api.entities.Guild;
import me.syari.ss.discord.annotations.DeprecatedSince;
import me.syari.ss.discord.annotations.ForRemoval;

/**
 * Indicates that a {@link Guild Guild} is not {@link Guild#isAvailable() available}
 * <br>Thrown when an operation requires a Guild to be available and {@link Guild#isAvailable() Guild#isAvailable()} is {@code false}
 *
 * @deprecated This will be removed in favor of a better system which does not keep unavailable guilds in cache in the first place.
 */
@Deprecated
@ForRemoval
@DeprecatedSince("4.1.0")
public class GuildUnavailableException extends RuntimeException
{
    /**
     * Creates a new GuildUnavailableException instance
     */
    public GuildUnavailableException()
    {
        this("This operation is not possible due to the Guild being temporarily unavailable");
    }

    /**
     * Creates a new GuildUnavailableException instance
     *
     * @param reason
     *        The reason for this Exception
     */
    public GuildUnavailableException(String reason)
    {
        super(reason);
    }
}
