package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.utils.TimeUtil;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;


public interface ISnowflake {

    @Nonnull
    default String getId() {
        return Long.toUnsignedString(getIdLong());
    }


    long getIdLong();


    @Nonnull
    default OffsetDateTime getTimeCreated() {
        return TimeUtil.getTimeCreated(getIdLong());
    }
}
