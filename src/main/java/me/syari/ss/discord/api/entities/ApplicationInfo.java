

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;


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
