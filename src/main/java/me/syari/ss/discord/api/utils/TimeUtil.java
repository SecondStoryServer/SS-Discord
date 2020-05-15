

package me.syari.ss.discord.api.utils;

import me.syari.ss.discord.api.entities.ISnowflake;
import me.syari.ss.discord.api.entities.MessageHistory;
import me.syari.ss.discord.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil
{
    public static final long DISCORD_EPOCH = 1420070400000L;
    public static final long TIMESTAMP_OFFSET = 22;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    /**
     * Converts the provided epoch millisecond timestamp to a Discord Snowflake.
     * <br>This can be used as a marker/pivot for {@link MessageHistory MessageHistory} creation.
     *
     * @param  millisTimestamp
     *         The epoch millis to convert
     *
     * @return Shifted epoch millis for Discord
     */
    public static long getDiscordTimestamp(long millisTimestamp)
    {
        return (millisTimestamp - DISCORD_EPOCH) << TIMESTAMP_OFFSET;
    }

    /**
     * Gets the creation-time of a JDA-entity by doing the reverse snowflake algorithm on its id.
     * This returns the creation-time of the actual entity on Discords side, not inside JDA.
     *
     * @param  entityId
     *         The id of the JDA entity where the creation-time should be determined for
     *
     * @return The creation time of the JDA entity as OffsetDateTime
     */
    @Nonnull
    public static OffsetDateTime getTimeCreated(long entityId)
    {
        long timestamp = (entityId >>> TIMESTAMP_OFFSET) + DISCORD_EPOCH;
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(timestamp);
        return OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId());
    }

    /**
     * Gets the creation-time of a JDA-entity by doing the reverse snowflake algorithm on its id.
     * This returns the creation-time of the actual entity on Discords side, not inside JDA.
     *
     * @param  entity
     *         The JDA entity where the creation-time should be determined for
     *
     * @throws IllegalArgumentException
     *         If the provided entity is {@code null}
     *
     * @return The creation time of the JDA entity as OffsetDateTime
     */
    @Nonnull
    public static OffsetDateTime getTimeCreated(@Nonnull ISnowflake entity)
    {
        Checks.notNull(entity, "Entity");
        return getTimeCreated(entity.getIdLong());
    }

    /**
     * Returns a prettier String-representation of a OffsetDateTime object
     *
     * @param  time
     *         The OffsetDateTime object to format
     *
     * @return The String of the formatted OffsetDateTime
     */
    @Nonnull
    public static String getDateTimeString(@Nonnull OffsetDateTime time)
    {
        return time.format(dtFormatter);
    }
}
