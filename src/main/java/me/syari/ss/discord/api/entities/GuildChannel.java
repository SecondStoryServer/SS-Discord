package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


public interface GuildChannel extends ISnowflake, Comparable<GuildChannel> {

    @Nonnull
    ChannelType getType();


    @Nonnull
    String getName();


    @Nonnull
    Guild getGuild();


    @Nullable
    Category getParent();


    @Nonnull
    List<Member> getMembers();


    int getPositionRaw();


    @Nonnull
    JDA getJDA();


    @Nullable
    PermissionOverride getPermissionOverride(@Nonnull IPermissionHolder permissionHolder);


    @Nonnull
    List<PermissionOverride> getPermissionOverrides();


}
