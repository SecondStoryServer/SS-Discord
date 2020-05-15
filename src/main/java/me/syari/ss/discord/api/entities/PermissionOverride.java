
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.PermissionOverrideAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;


public interface PermissionOverride extends ISnowflake
{

    long getAllowedRaw();


    long getInheritRaw();


    long getDeniedRaw();


    @Nonnull
    EnumSet<Permission> getAllowed();


    @Nonnull
    EnumSet<Permission> getInherit();


    @Nonnull
    EnumSet<Permission> getDenied();


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


    boolean isMemberOverride();


    boolean isRoleOverride();


    @Nonnull
    PermissionOverrideAction getManager();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();
}
