

package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.Permission;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.EnumSet;


public interface IPermissionHolder extends ISnowflake
{
    
    @Nonnull
    Guild getGuild();

    
    @Nonnull
    EnumSet<Permission> getPermissions();


    boolean hasPermission(@Nonnull Permission... permissions);

    
    boolean hasPermission(@Nonnull Collection<Permission> permissions);

    
    boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions);

    
    boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions);
}
