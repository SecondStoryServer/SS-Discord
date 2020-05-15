

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface ApplicationInfo extends ISnowflake
{


    @Nonnull
    String getDescription();


    @Nullable
    String getIconId();


    @Nonnull
    JDA getJDA();


    @Nonnull
    String getName();


}
