package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.internal.entities.Guild;
import org.jetbrains.annotations.NotNull;

public interface GuildChannel extends ISnowflake, Comparable<GuildChannel> {
    @NotNull
    String getName();


    @NotNull
    Guild getGuild();


    int getPositionRaw();


}
