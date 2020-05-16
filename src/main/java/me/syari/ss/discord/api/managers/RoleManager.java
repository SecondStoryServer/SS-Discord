package me.syari.ss.discord.api.managers;

import me.syari.ss.discord.api.entities.Role;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;


public interface RoleManager extends Manager<RoleManager> {

    long NAME = 0x1;

    long COLOR = 0x2;

    long PERMISSION = 0x4;

    long HOIST = 0x8;

    long MENTIONABLE = 0x10;


    @Nonnull
    @Override
    RoleManager reset(long fields);


    @Nonnull
    @Override
    RoleManager reset(long... fields);


    @Nonnull
    Role getRole();


    @Nonnull
    @CheckReturnValue
    RoleManager setName(@Nonnull String name);


    @Nonnull
    @CheckReturnValue
    RoleManager setPermissions(long perms);
}
