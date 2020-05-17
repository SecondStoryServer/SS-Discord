package me.syari.ss.discord.api.utils;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {
    public static final long DISCORD_EPOCH = 1420070400000L;
    public static final long TIMESTAMP_OFFSET = 22;


    @NotNull
    public static OffsetDateTime getTimeCreated(long entityId) {
        long timestamp = (entityId >>> TIMESTAMP_OFFSET) + DISCORD_EPOCH;
        Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmt.setTimeInMillis(timestamp);
        return OffsetDateTime.ofInstant(gmt.toInstant(), gmt.getTimeZone().toZoneId());
    }


}
