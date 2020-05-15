
package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.Permission;
import me.syari.ss.discord.api.exceptions.HierarchyException;
import me.syari.ss.discord.api.exceptions.InsufficientPermissionException;
import me.syari.ss.discord.api.exceptions.PermissionException;
import me.syari.ss.discord.api.requests.ErrorResponse;
import me.syari.ss.discord.api.requests.RestAction;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;
import me.syari.ss.discord.api.requests.restaction.RoleAction;
import me.syari.ss.discord.api.managers.RoleManager;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;


public interface Role extends IMentionable, IPermissionHolder, Comparable<Role>
{

    int DEFAULT_COLOR_RAW = 0x1FFFFFFF; // java.awt.Color fills the MSB with FF, we just use 1F to provide better consistency


    int getPosition();


    int getPositionRaw();


    @Nonnull
    String getName();


    boolean isManaged();


    boolean isHoisted();


    boolean isMentionable();


    long getPermissionsRaw();


    @Nullable
    Color getColor();


    int getColorRaw();


    boolean isPublicRole();


    boolean canInteract(@Nonnull Role role);


    @Nonnull
    Guild getGuild();


    @Nonnull
    @CheckReturnValue
    RoleAction createCopy(@Nonnull Guild guild);


    @Nonnull
    @CheckReturnValue
    default RoleAction createCopy()
    {
        return createCopy(getGuild());
    }


    @Nonnull
    RoleManager getManager();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    JDA getJDA();
}
