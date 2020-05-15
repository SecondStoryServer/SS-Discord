

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;


public interface ApplicationInfo extends ISnowflake
{

    boolean doesBotRequireCodeGrant();


    @Nonnull
    String getDescription();


    @Nullable
    String getIconId();


    @Nullable
    String getIconUrl();


    @Nullable
    ApplicationTeam getTeam();


    @Nonnull
    default String getInviteUrl(@Nullable Collection<Permission> permissions)
    {
        return getInviteUrl(null, permissions);
    }


    @Nonnull
    default String getInviteUrl(@Nullable Permission... permissions)
    {
        return getInviteUrl(null, permissions);
    }


    @Nonnull
    String getInviteUrl(@Nullable String guildId, @Nullable Collection<Permission> permissions);


    @Nonnull
    default String getInviteUrl(long guildId, @Nullable Collection<Permission> permissions)
    {
        return getInviteUrl(Long.toUnsignedString(guildId), permissions);
    }


    @Nonnull
    default String getInviteUrl(@Nullable String guildId, @Nullable Permission... permissions)
    {
        return getInviteUrl(guildId, permissions == null ? null : Arrays.asList(permissions));
    }


    @Nonnull
    default String getInviteUrl(long guildId, @Nullable Permission... permissions)
    {
        return getInviteUrl(Long.toUnsignedString(guildId), permissions);
    }


    @Nonnull
    JDA getJDA();


    @Nonnull
    String getName();


    @Nonnull
    User getOwner();


    boolean isBotPublic();
}
