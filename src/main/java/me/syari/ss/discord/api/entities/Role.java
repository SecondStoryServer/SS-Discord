package me.syari.ss.discord.api.entities;

import me.syari.ss.discord.api.JDA;
import me.syari.ss.discord.api.managers.RoleManager;
import me.syari.ss.discord.api.requests.restaction.AuditableRestAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;


public interface Role extends IMentionable, IPermissionHolder, Comparable<Role> {

    int DEFAULT_COLOR_RAW = 0x1FFFFFFF; // java.awt.Color fills the MSB with FF, we just use 1F to provide better consistency


    int getPosition();


    int getPositionRaw();


    @Nonnull
    String getName();


    long getPermissionsRaw();


    boolean isPublicRole();


    @Nonnull
    Guild getGuild();


    @Nonnull
    RoleManager getManager();


    @Nonnull
    @CheckReturnValue
    AuditableRestAction<Void> delete();


    @Nonnull
    JDA getJDA();
}
