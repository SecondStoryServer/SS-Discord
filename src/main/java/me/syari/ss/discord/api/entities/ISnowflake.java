package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public interface ISnowflake {
    @NotNull
    default String getId() {
        return Long.toUnsignedString(getIdLong());
    }

    long getIdLong();

    @NotNull
    default OffsetDateTime getTimeCreated() {
        return TimeUtil.getTimeCreated(getIdLong());
    }
}
