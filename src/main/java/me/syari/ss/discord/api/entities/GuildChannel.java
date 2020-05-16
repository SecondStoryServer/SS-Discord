package me.syari.ss.discord.api.entities;

import javax.annotation.Nonnull;


public interface GuildChannel extends ISnowflake, Comparable<GuildChannel> {

    @Nonnull
    ChannelType getType();


    @Nonnull
    String getName();


    @Nonnull
    Guild getGuild();


    int getPositionRaw();


}
