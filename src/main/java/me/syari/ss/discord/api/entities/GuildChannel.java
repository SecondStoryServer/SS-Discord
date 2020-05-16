package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface GuildChannel extends ISnowflake, Comparable<GuildChannel> {

    @Nonnull
    ChannelType getType();


    @Nonnull
    String getName();


    @Nonnull
    Guild getGuild();


    int getPositionRaw();


    @Nonnull
    JDA getJDA();


    @Nullable
    PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder);


}
