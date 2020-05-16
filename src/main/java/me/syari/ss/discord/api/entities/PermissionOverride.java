package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface PermissionOverride extends ISnowflake {


    @Nonnull
    JDA getJDA();


    @Nullable
    Member getMember();


    @Nullable
    Role getRole();


    @Nonnull
    GuildChannel getChannel();


    @Nonnull
    Guild getGuild();


}
