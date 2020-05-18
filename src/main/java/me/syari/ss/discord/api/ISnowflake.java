package me.syari.ss.discord.api;

import org.jetbrains.annotations.NotNull;

public interface ISnowflake {
    @NotNull
    default String getId() {
        return Long.toUnsignedString(getIdLong());
    }

    long getIdLong();
}
