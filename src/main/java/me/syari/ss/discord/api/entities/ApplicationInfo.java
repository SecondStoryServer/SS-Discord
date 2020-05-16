

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;


public interface ApplicationInfo extends ISnowflake
{


    @Nonnull
    JDA getJDA();


    @Nonnull
    String getName();


}
