package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.internal.entities.Guild;

import javax.annotation.Nonnull;


public interface GuildChannel extends ISnowflake, Comparable<GuildChannel> {
    @Nonnull
    String getName();


    @Nonnull
    Guild getGuild();


    int getPositionRaw();


}
